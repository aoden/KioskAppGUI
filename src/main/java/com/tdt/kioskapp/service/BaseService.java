package com.tdt.kioskapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdt.kioskapp.dto.AccessTokenDTO;
import com.tdt.kioskapp.dto.KeyDTO;
import com.tdt.kioskapp.dto.SlideDTO;
import com.tdt.kioskapp.model.Key;
import com.tdt.kioskapp.repository.KeyRepository;
import com.tdt.kioskapp.ui.KioskUI;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class BaseService extends AbstractService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private KeyRepository keyRepository;
    @Value("${url}")
    private String baseURL;

    public File findFile(String location, String fileName) {

        File folder = new File(location);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile() || file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    /**
     * read file from a directory, this method only return 1 file
     *
     * @param location dir location
     * @return the only file inside directory
     */
    public File readAllFiles(String location) {

        File folder = new File(location);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            return null;
        } else {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    return file;
                }
            }
        }
        return null;
    }

    public Map<String, SlideDTO> readManifest(String tokenKey) throws IOException, ZipException {

        return KioskUI.playCount > 1 ? processData() : processData(getData(login(register(tokenKey))));
    }

    public Map<String, SlideDTO> readManifest() throws IOException, ZipException {
        List<Key> keys = keyRepository.findAll();
        if (keys == null || keys.isEmpty()) {
            return null;
        }
        return KioskUI.playCount > 1 ? processData() : processData(getData(login(keys.get(0).getKey())));
    }

    public boolean registered() {

        return keyRepository.findAll() == null || keyRepository.findAll().size() == 0 ? false : true;
    }

    protected Map<String, SlideDTO> processData(byte[] data) throws IOException, ZipException {
        File file = new File("data.zip");
        FileUtils.writeByteArrayToFile(file, data);
        ZipFile zipFile = new ZipFile(file);
        zipFile.extractAll(TEMP_DIR);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(TEMP_DIR + "/" + MANIFEST_JSON), new TypeReference<Map<String, SlideDTO>>() {
        });
    }

    protected Map<String, SlideDTO> processData() throws IOException, ZipException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(TEMP_DIR + "/" + MANIFEST_JSON), new TypeReference<Map<String, SlideDTO>>() {
        });
    }

    protected byte[] getData(String key) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseURL + "package")
                .queryParam("access_token", key);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "*/*");
        headers.add("Accept-Encoding", "gzip, deflate, sdch");
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(builder.build().encode().toUri(),
                HttpMethod.GET,
                httpEntity,
                byte[].class).getBody();
    }

    protected String login(String key) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseURL + "auth")
                .queryParam("client_id", ID)
                .queryParam("client_secret", SECRET)
                .queryParam("key", key)
                .queryParam("grant_type", "key_provider");
        AccessTokenDTO dto = restTemplate.postForEntity(builder.build().encode().toUri(), null, AccessTokenDTO.class).getBody();
        return dto.getAccessToken();
    }

    @Transactional
    public String register(String key) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseURL + "/activate/");
        builder.path(key);
        KeyDTO keyDTO = restTemplate.postForEntity(builder.build().encode().toUri(), null, KeyDTO.class).getBody();
        keyRepository.save(keyDTO.toEntity());
        return keyDTO.getKey();
    }
}
