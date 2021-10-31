package com.example.securityoauth2.controller;

import com.example.securityoauth2.dto.OAuthToken;
import com.google.gson.Gson;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/oauth2/callback")
public class Oauth2Controller {

    @Autowired
    private Gson gson;

    @GetMapping
    public OAuthToken showEmployees(String code) {

        String credentials = "testClientId:testSecret"; // 클라이언트 ID, secret
        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        // Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Basic " + encodedCredentials);

        // Param 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", "http://localhost:8082/oauth2/callback");

        // Header와 Param을 HttpEntity에 담기
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // 토큰값을 발급 받아서 ResponseEntity에 저장
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8082/oauth/token", request, String.class);

        // 리턴값을 OAuthToken에 담아서 리턴
        if(response.getStatusCode() == HttpStatus.OK)
            return gson.fromJson(response.getBody(), OAuthToken.class);
        return null;
    }
}