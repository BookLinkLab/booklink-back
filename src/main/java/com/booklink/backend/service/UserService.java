package com.booklink.backend.service;

import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.dto.user.UpdateUserDTO;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.dto.user.UserResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    UserDto registerUser(CreateUserDto userDto);
    UserResponseDto getUserByEmail(String email);
    UserResponseDto getUserWithPassword(Long id);
    List<UserResponseDto> getAllUsers();
    void updateUser(long id, UpdateUserDTO updateUserDTO);
}