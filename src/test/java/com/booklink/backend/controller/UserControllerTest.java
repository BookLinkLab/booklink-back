package com.booklink.backend.controller;

import com.booklink.backend.dto.user.UpdateUserDTO;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.model.User;
import com.booklink.backend.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.Assert;

import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)


public class UserControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;

    private final String baseUrl = "/user";




    @BeforeEach
    void setup() {
        User user = User.builder()
                .username("Facundo")
                .email("facundo@gmail.com")
                .password("123")
                .build();
        restTemplate.postForEntity(baseUrl, user, User.class);
    }


    @Test
    void updateUser(){

        Long userIdToUpdate = 1L;


        UpdateUserDTO updateUserDTO = UpdateUserDTO.builder()
                .username("Joaquin")
                .email("joaquin@gmail.com")
                .password("abc")
                .build();

        ResponseEntity<User> response = restTemplate.exchange(
                baseUrl + "/1", HttpMethod.PUT, new HttpEntity<>(updateUserDTO), User.class
        );
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<User> userOpt = userRepository.findById(userIdToUpdate);
        User user = userOpt.orElseThrow(() -> new NotFoundException("User %d not found".formatted(userIdToUpdate)));
        Assertions.assertEquals("Joaquin", user.getUsername());
        Assertions.assertEquals("joaquin@gmail.com", user.getEmail());
        Assertions.assertEquals("abc", user.getPassword());

    }

}
