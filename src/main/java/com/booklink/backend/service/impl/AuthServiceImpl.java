package com.booklink.backend.service.impl;

import com.booklink.backend.dto.LoginRequestDto;
import com.booklink.backend.dto.LoginResponseDto;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.dto.user.UserResponseDto;
import com.booklink.backend.dto.user.UserWithPasswordDto;
import com.booklink.backend.exception.WrongCredentialsException;
import com.booklink.backend.model.User;
import com.booklink.backend.service.AuthService;
import com.booklink.backend.service.UserService;
import com.booklink.backend.utils.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        UserWithPasswordDto user = userService.getUserByEmail(loginRequestDto.getEmail()).getUserWithPasswordDto();
        if (passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getEmail());
            String token = jwtUtil.generateToken(userDto.getEmail());
            return LoginResponseDto.builder().user(userDto).token(token).build();
        }
        else {
            throw new WrongCredentialsException("Wrong credentials");
        }
    }
}
