package com.booklink.backend.controller;

import com.booklink.backend.dto.LoginResponseDto;
import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.dto.user.UpdateUserDto;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.dto.user.UserProfileDto;
import com.booklink.backend.service.UserService;
import com.booklink.backend.utils.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final SecurityUtil securityUtil;

    public UserController(UserService userService, SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<LoginResponseDto> registerUser(@Valid @RequestBody CreateUserDto createUserDto) {
        LoginResponseDto userDto = this.userService.registerUser(createUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @GetMapping()
    public List<UserDto> getAllUsers() {
        return this.userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserProfileDto getUserProfile(@PathVariable Long id) {
        Long userWhoSearchesId = securityUtil.getLoggedUserId();
        return this.userService.getUserById(id, userWhoSearchesId);
    }


    @PatchMapping("{id}")
    public UserDto updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserDto updateUserDTO) {
        return this.userService.updateUser(id, updateUserDTO);
    }
}
