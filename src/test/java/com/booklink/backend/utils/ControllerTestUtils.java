package com.booklink.backend.utils;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class ControllerTestUtils {

    public static void setOutputStreamingFalse(TestRestTemplate restTemplate){
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setOutputStreaming(false);
            restTemplate.getRestTemplate().setRequestFactory(requestFactory);
    }
}
