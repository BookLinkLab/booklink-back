package com.booklink.backend.service;

import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.dto.user.UserResponseDto;

import java.util.List;

public interface UserService {
    UserDto registerUser(CreateUserDto userDto);
    UserResponseDto getUserByEmail(String email);
    List<UserResponseDto> getAllUsers();
}