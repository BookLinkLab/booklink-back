package com.booklink.backend.controller;


import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.model.Notification;
import com.booklink.backend.model.NotificationType;
import com.booklink.backend.repository.NotificationRepository;
import com.booklink.backend.utils.ControllerTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class NotificationControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    private final String baseUrl = "/notification";
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private ControllerTestUtils utils;

    @BeforeEach
    public void setUp(){

        UserDto user = utils.createUserAndLogIn("user", "user@email.com", "password", restTemplate);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer tu-token-jwt");


        Notification notificationToSave = Notification.builder()
                .type(NotificationType.POST)
                .postAuthorId(1L)
                .receiverId(2L)
                .forumId(1L)
                .postId(1L)
                .createdDate(new Date())
                .build();
        notificationRepository.save(notificationToSave);
    }

    @Test
    public void deleteNotificationTest(){

        Notification notificationToSave = Notification.builder()
                .type(NotificationType.POST)
                .postAuthorId(1L)
                .receiverId(2L)
                .forumId(1L)
                .postId(1L)
                .createdDate(new Date())
                .build();

        restTemplate.postForEntity(baseUrl, notificationToSave, Notification.class);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + 1, HttpMethod.DELETE, new HttpEntity<>(null), String.class);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Notificacion eliminada", response.getBody());
    }


}
