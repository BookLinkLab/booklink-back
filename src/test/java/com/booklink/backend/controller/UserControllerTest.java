package com.booklink.backend.controller;

import com.booklink.backend.dto.LoginRequestDto;
import com.booklink.backend.dto.LoginResponseDto;
import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.dto.user.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.booklink.backend.dto.user.UpdateUserDTO;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.model.User;
import com.booklink.backend.repository.UserRepository;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

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
        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("user")
                .email("user@email.com")
                .password("password")
                .build();
        restTemplate.postForEntity(baseUrl, createUserDto, UserDto.class);

        User user = User.builder()
                .username("Facundo")
                .email("facundo@gmail.com")
                .password("123")
                .build();

        userRepository.save(user);


        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("user@email.com")
                .password("password")
                .build();
        ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity(
                "/auth", loginRequestDto, LoginResponseDto.class
        );
        String token = response.getBody().getToken();
        restTemplate.getRestTemplate().setInterceptors(
                List.of((request, body, execution) -> {
                    request.getHeaders().add("Authorization", "Bearer " + token);
                    return execution.execute(request, body);
                })
        );

    }

    @Test
    void registerUser() {
        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("newUser")
                .email("newUser@email.com")
                .password("password")
                .build();

        ResponseEntity<UserDto> response = restTemplate.exchange(
                baseUrl, HttpMethod.POST, new HttpEntity<>(createUserDto), UserDto.class
        );
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserDto responseUser = UserDto.builder()
                .id(3L)
                .username("newUser")
                .email("newUser@email.com")
                .build();
        assertEquals(response.getBody(), responseUser);
    }

    @Test
    void getAllUsers() {
        ResponseEntity<List<UserDto>> response = this.restTemplate.exchange(
                this.baseUrl, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                }
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void existingEmailException() {
        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("newUser")
                .email("user@email.com")
                .password("password")
                .build();
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl, HttpMethod.POST, new HttpEntity<>(createUserDto), String.class
        );
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void existingUsernameException() {
        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("user")
                .email("user@email.com")
                .password("password")
                .build();
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl, HttpMethod.POST, new HttpEntity<>(createUserDto), String.class
        );
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void invalidUsernameException() {
        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("d#")
                .email("user@email.com")
                .password("password")
                .build();
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl, HttpMethod.POST, new HttpEntity<>(createUserDto), String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void invalidPasswordException() {
        CreateUserDto createUserDto = CreateUserDto.builder()
                .username("newUser")
                .email("user@email.com")
                .password("$")
                .build();
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl, HttpMethod.POST, new HttpEntity<>(createUserDto), String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getUserById(){
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        restTemplate.getRestTemplate().setRequestFactory(requestFactory);



        ResponseEntity<UserDto> response = restTemplate.exchange(
                baseUrl + "/1", HttpMethod.GET, null, UserDto.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void updateUserTest(){
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        restTemplate.getRestTemplate().setRequestFactory(requestFactory);

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

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        restTemplate.getRestTemplate().setRequestFactory(requestFactory);

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

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        restTemplate.getRestTemplate().setRequestFactory(requestFactory);

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
