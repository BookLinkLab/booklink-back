package com.booklink.backend.service;

import com.booklink.backend.dto.LoginResponseDto;
import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.dto.user.UpdateUserDTO;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.dto.LoginRequestDto;
import com.booklink.backend.dto.user.UserProfileDto;

import java.util.List;

public interface UserService {
    LoginResponseDto registerUser(CreateUserDto userDto);

    UserProfileDto getUserById(Long id);

    UserDto getUserByUsername(String username);
    List<UserDto> getAllUsers();
    UserDto updateUser(long id, UpdateUserDTO updateUserDTO);
    UserDto authorizedGetByEmail(LoginRequestDto loginRequestDto);
}
