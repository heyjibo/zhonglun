// src/main/java/org/example/zhonglun/service/LogisticsService.java
package org.example.zhonglun.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Service
public class LogisticsService {

    @Value("${kdn.ebusiness-id}")
    private String eBusinessID;

    @Value("${kdn.app-key}")
    private String appKey;

    @Value("${kdn.subscribe-url}")
    private String subscribeUrl;

    @Value("${kdn.callback-url}")
    private String callbackUrl;

    private final RestTemplate restTemplate;

    // 确保在主应用配置类中有 @Bean public RestTemplate restTemplate() { return new RestTemplate(); }
    public LogisticsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String subscribeToTracking(String shipperCode, String logisticCode) throws Exception {
        String requestData = String.format(
                "{\"ShipperCode\":\"%s\",\"LogisticCode\":\"%s\",\"CallBack\":\"%s\"}",
                shipperCode, logisticCode, callbackUrl
        );

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("EBusinessID", eBusinessID);
        params.add("RequestType", "1008");
        params.add("RequestData", URLEncoder.encode(requestData, StandardCharsets.UTF_8.toString()));
        params.add("DataSign", generateDataSign(requestData));
        params.add("DataType", "2");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        return restTemplate.postForObject(subscribeUrl, request, String.class);
    }

    private String generateDataSign(String requestData) throws Exception {
        String toSign = requestData + appKey;
        String md5Value = md5(toSign, StandardCharsets.UTF_8.toString());
        return URLEncoder.encode(Base64.getEncoder().encodeToString(md5Value.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8.toString());
    }

    private String md5(String str, String charset) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes(charset));
        byte[] result = md.digest();
        StringBuilder sb = new StringBuilder(32);
        for (byte b : result) {
            int val = b & 0xff;
            if (val < 16) sb.append("0");
            sb.append(Integer.toHexString(val));
        }
        return sb.toString().toLowerCase();
    }
}
