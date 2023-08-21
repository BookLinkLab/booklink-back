package com.booklink.backend.controller;


import com.booklink.backend.dto.LoginRequestDto;
import com.booklink.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

  @Autowired
  private final AuthService authService;

  @PostMapping("/auth")
  public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequestDto loginRequestDto)
          throws Exception {
    try {
      return ResponseEntity.ok(this.authService.login(loginRequestDto));
    }
    catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }



}
