package com.booklink.backend.controller;


import com.booklink.backend.dto.notification.NotificationViewDto;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.http.HttpEntity;

import java.util.Date;
import java.util.List;

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
    public void setUp() {
        UserDto user = utils.createUserAndLogIn("user", "user@email.com", "password", restTemplate);

        Notification notificationToSave = Notification.builder()
                .type(NotificationType.POST)
                .postAuthorId(1L)
                .receiverId(user.getId())
                .forumId(1L)
                .postId(1L)
                .createdDate(new Date())
                .build();
        notificationRepository.save(notificationToSave);
    }

    @Test
    public void deleteNotificationTest() {

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + 1, HttpMethod.DELETE, new HttpEntity<>(null), String.class);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Notificacion eliminada", response.getBody());
    }

    @Test
    public void getNotificationsTest() {
        ResponseEntity<List<NotificationViewDto>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                new HttpEntity<>(null),
                new ParameterizedTypeReference<List<NotificationViewDto>>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("@lucia21 creó una nueva publicación en Harry Potter!", response.getBody().get(0).getContent());
    }
}
