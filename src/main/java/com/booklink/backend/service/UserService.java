package com.booklink.backend.service;

import com.booklink.backend.dto.CreateUserDto;
import com.booklink.backend.dto.UserDto;
import com.booklink.backend.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    UserDto registerUser(CreateUserDto userDto);

    UserResponseDto getUser(Long id);

    UserResponseDto getUserWithPassword(Long id);

    List<UserResponseDto> getAllUsers();
}