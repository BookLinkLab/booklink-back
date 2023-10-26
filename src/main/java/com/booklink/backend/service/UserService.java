package com.booklink.backend.service;

import com.booklink.backend.dto.LoginResponseDto;
import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.dto.user.UpdateUserDto;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.dto.LoginRequestDto;
import com.booklink.backend.dto.user.UserProfileDto;
import com.booklink.backend.model.User;

import java.util.List;

public interface UserService {
    LoginResponseDto registerUser(CreateUserDto userDto);
    UserProfileDto getUserById(Long id, Long userWhoSearchesId);
    User getUserEntityById(Long id);
    List<UserDto> getAllUsers();
    UserDto updateUser(long id, UpdateUserDto updateUserDTO);
    UserDto authorizedGetByEmail(LoginRequestDto loginRequestDto);
    UserDto setUserPrivacy(Long id);
}
