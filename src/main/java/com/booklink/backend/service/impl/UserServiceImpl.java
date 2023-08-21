package com.booklink.backend.service.impl;

import com.booklink.backend.dto.CreateUserDto;
import com.booklink.backend.dto.UserDto;
import com.booklink.backend.dto.UserResponseDto;
import com.booklink.backend.dto.UserWithPasswordDto;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.exception.SpecialCharacterException;
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
        if (userDto.getPassword().contains("$"))
            throw new SpecialCharacterException("Special characters not allowed here");
        String encryptedPassword = this.passwordEncoder.encode(userDto.getPassword());
        User userToSave = User.from(userDto, encryptedPassword);
        User savedUser = this.userRepository.save(userToSave);
        return UserDto.from(savedUser);
    }

    @Override
    public UserResponseDto getUser(Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        User user = userOptional.orElseThrow(() -> new NotFoundException("User %d not found".formatted(id)));
        return UserResponseDto.builder().userDto(UserDto.from(user)).build();
    }

    @Override
    public UserResponseDto getUserWithPassword(Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        User user = userOptional.orElseThrow(() -> new NotFoundException("User %d not found".formatted(id)));
        return UserResponseDto.builder().userWithPasswordDto(UserWithPasswordDto.from(user)).build();
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        List<User> users = this.userRepository.findAll();
        return users.stream().map(UserResponseDto::from).toList();
    }
}
