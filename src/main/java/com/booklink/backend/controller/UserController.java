package com.booklink.backend.controller;

import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.dto.user.UpdateUserDTO;
import com.booklink.backend.dto.user.UserResponseDto;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> registerUser(@Valid @RequestBody CreateUserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (ObjectError fieldError : result.getAllErrors()) {
                errors.append(fieldError.getDefaultMessage()).append("\n");
            }
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.registerUser(userDto));
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username or email already exists");
        }
    }

    @GetMapping("{email}")
    public UserResponseDto getUserByEmail(@PathVariable String email) {
        return this.userService.getUserByEmail(email);
    }

    @GetMapping()
    public List<UserResponseDto> getAllUsers() {
        return this.userService.getAllUsers();
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        try {
            this.userService.updateUser(id, updateUserDTO);
            return ResponseEntity.ok().build();
        }
        catch (NotFoundException exception){
            return ResponseEntity.notFound().build();
        }
     }


}