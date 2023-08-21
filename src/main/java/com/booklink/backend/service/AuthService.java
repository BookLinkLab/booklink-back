package com.booklink.backend.service;

import com.booklink.backend.dto.LoginRequestDto;
import com.booklink.backend.dto.LoginResponseDto;

public interface AuthService {
//    TODO: AuthServiceImpl class, login method should throw an exception if the user is not found or the credentials are incorrect
    LoginResponseDto login(LoginRequestDto loginRequestDto);

}
