package com.tdt.kioskapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdt.kioskapp.dto.KeyDTO;
import com.tdt.kioskapp.dto.SlideDTO;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class BaseService extends AbstractService {

    @Autowired
    private RestTemplate restTemplate;
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

    public Map<String, SlideDTO> readManifest(String key) throws IOException, ZipException, ParseException {

        byte[] data = getData(login(register(key)));
        File file = new File("data.zip");
        FileUtils.writeByteArrayToFile(file, data);
        ZipFile zipFile = new ZipFile(file);
        zipFile.extractAll(TEMP_DIR);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(TEMP_DIR + "/" + MANIFEST_JSON), new TypeReference<Map<String, SlideDTO>>() {
        });
    }

    protected byte[] getData(String key) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseURL + "/package")
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
                .fromHttpUrl(baseURL + "/oauth")
                .queryParam("client_id", ID)
                .queryParam("client_secret", SECRET)
                .queryParam("key", key);
        KeyDTO keyDTO = restTemplate.getForObject(builder.build().encode().toUri(), KeyDTO.class);
        return keyDTO.getKey();
    }

    protected String register(String key) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseURL + "/link");
        builder.queryParam("key", key);
        KeyDTO keyDTO = restTemplate.getForObject(builder.build().encode().toUri(), KeyDTO.class);
        return keyDTO.getKey();
    }
}
