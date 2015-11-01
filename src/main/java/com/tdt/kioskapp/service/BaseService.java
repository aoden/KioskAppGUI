package com.tdt.kioskapp.service;

import com.tdt.kioskapp.dto.KeyDTO;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class BaseService extends AbstractService {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${url}")
    private String baseURL;

    public JSONObject readManifest(String key) throws IOException, ZipException, ParseException {

        byte[] data = ArrayUtils.toPrimitive(getData(login(register(key))));
        File file = new File("data.zip");
        FileUtils.writeByteArrayToFile(file, data);
        ZipFile zipFile = new ZipFile(file);
        zipFile.extractAll(TEMP_DIR);
        JSONParser jsonParser = new JSONParser();
        return (JSONObject) jsonParser.parse(new FileReader(TEMP_DIR + "/" + MANIFEST_JSON));
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