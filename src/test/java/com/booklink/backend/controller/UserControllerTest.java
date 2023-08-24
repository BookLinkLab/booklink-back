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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.Assert;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        userRepository.save(user);
    }


    @Test
    void updateUserTest(){

        Long userIdToUpdate = 1L;


        UpdateUserDTO updateUserDTO = UpdateUserDTO.builder()
                .username("Joaquin")
                .email("joaquin@gmail.com")
                .password("abc")
                .build();


        ResponseEntity<User> response = UpdateRequest(userIdToUpdate, updateUserDTO,User.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<User> userOpt = userRepository.findById(userIdToUpdate);
        User user = userOpt.orElseThrow(() -> new NotFoundException("User %d not found".formatted(userIdToUpdate)));
        assertEquals("Joaquin", user.getUsername());
        assertEquals("joaquin@gmail.com", user.getEmail());

    }

    @Test
    void notFoundUser_404_Test(){
        UpdateUserDTO updateUserDTO = UpdateUserDTO.builder()
                .username("Joaquin")
                .email("joaquin@gmail.com")
                .password("abc")
                .build();

        Long userId = 80L;


        ResponseEntity<String> response = UpdateRequest(userId, updateUserDTO,String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        }

    @Test
    void invalidInput_400_Test() {
        Long userId = 1L;

        UpdateUserDTO updateUserDTO = UpdateUserDTO.builder()
                .username("Jo")
                .email("joaquin@gmail.com")
                .password("abc")
                .build();

        UpdateUserDTO updateUserDTO1 = UpdateUserDTO.builder()
                .username("Jo")
                .email("joaquin")
                .password("abc")
                .build();

        UpdateUserDTO updateUserDTO2 = UpdateUserDTO.builder()
                .username("Joaquin*#")
                .email("joaquin@gmail.com")
                .password("abc")
                .build();


        ResponseEntity<String> response = UpdateRequest(userId, updateUserDTO,String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ResponseEntity<String> response1 = UpdateRequest(userId, updateUserDTO1,String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());

        ResponseEntity<String> response2 = UpdateRequest(userId, updateUserDTO2,String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());

    }


    private <T> ResponseEntity<T> UpdateRequest(Long userId, UpdateUserDTO updateUserDTO, Class<T> responseType) {
        return restTemplate.exchange(
                baseUrl + "/" + userId, HttpMethod.PUT, new HttpEntity<>(updateUserDTO), responseType
        );
    }






}
