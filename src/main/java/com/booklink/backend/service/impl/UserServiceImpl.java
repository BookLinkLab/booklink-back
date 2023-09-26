package com.booklink.backend.service.impl;

import com.booklink.backend.dto.LoginRequestDto;
import com.booklink.backend.dto.LoginResponseDto;
import com.booklink.backend.dto.forum.ForumDto;
import com.booklink.backend.dto.forum.ForumDtoFactory;
import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.dto.user.*;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.exception.WrongCredentialsException;
import com.booklink.backend.model.Forum;
import com.booklink.backend.model.User;
import com.booklink.backend.repository.UserRepository;
import com.booklink.backend.service.UserService;
import com.booklink.backend.utils.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginResponseDto registerUser(CreateUserDto userDto) {
        String encryptedPassword = this.passwordEncoder.encode(userDto.getPassword());
        User userToSave = User.from(userDto, encryptedPassword);
        User savedUser = this.userRepository.save(userToSave);
        String token = jwtUtil.generateToken(savedUser.getId().toString());
        return LoginResponseDto.builder().user(UserDto.from(savedUser)).token(token).build();
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = this.userRepository.findAll();
        return users.stream().map(UserDto::from).toList();
    }

    @Override
    public UserProfileDto getUserById(Long id, Long userWhoSearchesId) {
        User user = getUserEntityById(id);
        List<ForumDto> forumsCreated = ForumDtoFactory.createForumDtoAndForumViewDtoWithIsMember(user.getForumsCreated(), userWhoSearchesId, ForumDto::from);
        List<ForumDto> forumsJoined = ForumDtoFactory.createForumDtoAndForumViewDtoWithIsMember(user.getForumsJoined(), userWhoSearchesId, ForumDto::from);
        return UserProfileDto.from(user, forumsCreated, forumsJoined);

    }

    @Override
    public User getUserEntityById(Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        return userOptional.orElseThrow(() -> new NotFoundException("El usuario %s no fue encontrado".formatted(id)));
    }

    @Override
    public UserDto updateUser(long id, UpdateUserDto updateUserDTO) {
        Optional<User> userOptional = this.userRepository.findById(id);
        User user = userOptional.orElseThrow(() -> new NotFoundException("El Usuario %s no fue encontrado".formatted(id)));
        if (updateUserDTO.getEmail() != null) {
            user.setEmail(updateUserDTO.getEmail());
        }
        if (updateUserDTO.getUsername() != null) {
            user.setUsername(updateUserDTO.getUsername());
        }
        if (updateUserDTO.getPassword() != null) {
            String encryptedPassword = this.passwordEncoder.encode(updateUserDTO.getPassword());
            user.setPassword(encryptedPassword);
        }
        return UserDto.from(userRepository.save(user));

    }

    @Override
    public UserDto authorizedGetByEmail(LoginRequestDto loginRequestDto) {
        Optional<User> userOptional = this.userRepository.findByEmail(loginRequestDto.getEmail());
        User user = userOptional.orElseThrow(() -> new NotFoundException("El usuario %s no fue encontrado".formatted(loginRequestDto.getEmail())));
        boolean passwordMatches = this.passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword());
        if (!passwordMatches) {
            throw new WrongCredentialsException("Credenciales invalidas");
        }
        return UserDto.from(user);
    }
}
