package com.booklink.backend.controller;


import com.booklink.backend.dto.LoginRequestDto;
import com.booklink.backend.dto.LoginResponseDto;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.exception.WrongCredentialsException;
import com.booklink.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/auth")
  public ResponseEntity<LoginResponseDto> createAuthenticationToken(@Valid @RequestBody LoginRequestDto loginRequestDto){
      return ResponseEntity.status(HttpStatus.OK).body(this.authService.login(loginRequestDto));
  }



}
