package com.booklink.backend.service;

import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto registerUser(CreateUserDto userDto);
    UserDto getUserById(Long id);
    List<UserDto> getAllUsers();
}