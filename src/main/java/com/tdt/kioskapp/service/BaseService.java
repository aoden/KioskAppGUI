package com.tdt.kioskapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tdt.kioskapp.dto.KeyDTO;
import com.tdt.kioskapp.dto.SlideDTO;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class BaseService extends AbstractService {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${url}")
    private String baseURL;

    public File readAllFiles(String location) {

        File folder = new File(location);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                return file;
            }
        }
    }

    public Map<String, SlideDTO> readManifest(String key) throws IOException, ZipException, ParseException {

        byte[] data = ArrayUtils.toPrimitive(getData(login(register(key))));
        File file = new File("data.zip");
        FileUtils.writeByteArrayToFile(file, data);
        ZipFile zipFile = new ZipFile(file);
        zipFile.extractAll(TEMP_DIR);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(TEMP_DIR + "/" + MANIFEST_JSON), new TypeReference<Map<String, SlideDTO>>() {
        });
    }

    protected Byte[] getData(String key) {

        Map<String, String> params = new HashMap<>();
        params.put("access_token", key);
        return restTemplate.getForObject(baseURL + "package", Byte[].class, params);
    }

    protected String login(String key) {

        Map<String, String> params = new HashMap<>();
        params.put("client_id", id);
        params.put("client_secret", secret);
        params.put("key", key);
        KeyDTO keyDTO = restTemplate.getForObject(baseURL + "oauth", KeyDTO.class, params);
        return keyDTO.getKey();
    }

    protected String register(String key) {

        Map<String, String> params = new HashMap<>();
        params.put("key", key);
        KeyDTO keyDTO = restTemplate.getForObject(baseURL + "link", KeyDTO.class, params);
        return keyDTO.getKey();
    }
}
