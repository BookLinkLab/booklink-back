package com.booklink.backend.service.impl;

import com.booklink.backend.dto.LoginRequestDto;
import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.dto.user.*;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.exception.WrongCredentialsException;
import com.booklink.backend.model.User;
import com.booklink.backend.repository.UserRepository;
import com.booklink.backend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto registerUser(CreateUserDto userDto) {
        String encryptedPassword = this.passwordEncoder.encode(userDto.getPassword());
        User userToSave = User.from(userDto, encryptedPassword);
        User savedUser = this.userRepository.save(userToSave);
        return UserDto.from(savedUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = this.userRepository.findAll();
        return users.stream().map(UserDto::from).toList();
    }

    @Override
    public UserProfileDto getUserById(Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        User user = userOptional.orElseThrow(() -> new NotFoundException("User %s not found".formatted(id)));
        return UserProfileDto.from(user);
    }

    @Override
    public UserDto updateUser(long id, UpdateUserDTO updateUserDTO) {
        Optional<User> userOptional = this.userRepository.findById(id);
        User user = userOptional.orElseThrow(() -> new NotFoundException("User %d not found".formatted(id)));
        user.setEmail(updateUserDTO.getEmail());
        user.setUsername(updateUserDTO.getUsername());
        String encryptedPassword = this.passwordEncoder.encode(updateUserDTO.getPassword());
        user.setPassword(encryptedPassword);
        return UserDto.from(userRepository.save(user));

    }

    @Override
    public UserDto authorizedGetByEmail(LoginRequestDto loginRequestDto) {
        Optional<User> userOptional = this.userRepository.findByEmail(loginRequestDto.getEmail());
        User user = userOptional.orElseThrow(() -> new NotFoundException("User %s not found".formatted(loginRequestDto.getEmail())));
        boolean passwordMatches = this.passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword());
        if (!passwordMatches) {
            throw new WrongCredentialsException("Wrong credentials");
        }
        return UserDto.from(user);
    }
}
