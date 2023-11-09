package com.booklink.backend.service.impl;

import com.booklink.backend.dto.LoginRequestDto;
import com.booklink.backend.dto.LoginResponseDto;
import com.booklink.backend.dto.forum.ForumDto;
import com.booklink.backend.dto.forum.ForumDtoFactory;
import com.booklink.backend.dto.post.LatestPostDto;
import com.booklink.backend.dto.post.PostPreviewDto;
import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.dto.user.UserDto;
import com.booklink.backend.dto.user.*;
import com.booklink.backend.exception.NotFoundException;
import com.booklink.backend.exception.WrongCredentialsException;
import com.booklink.backend.model.Post;
import com.booklink.backend.model.User;
import com.booklink.backend.repository.UserRepository;
import com.booklink.backend.service.PostService;
import com.booklink.backend.service.UserService;
import com.booklink.backend.utils.JwtUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ForumDtoFactory forumDtoFactory;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PostService postService;

    public UserServiceImpl(UserRepository userRepository, @Lazy ForumDtoFactory forumDtoFactory, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,@Lazy PostService postService) {
        this.userRepository = userRepository;
        this.forumDtoFactory = forumDtoFactory;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.postService = postService;
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
        if (user.isPrivacy() && !user.getId().equals(userWhoSearchesId)){
            return new UserProfileDto(user.getUsername(), true);
        }
        List<ForumDto> forumsCreated = forumDtoFactory.createForumDtoAndForumViewDtoWithIsMember(user.getForumsCreated(), userWhoSearchesId, ForumDto::from);
        List<ForumDto> forumsJoined = forumDtoFactory.createForumDtoAndForumViewDtoWithIsMember(user.getForumsJoined(), userWhoSearchesId, ForumDto::from);
        List<LatestPostDto> latestPosts = getUserLatestPosts(id, userWhoSearchesId);
        return UserProfileDto.from(user, forumsCreated, forumsJoined, latestPosts);

    }

    private List<LatestPostDto> getUserLatestPosts(Long id, Long userWhoSearchesId) {
        List<LatestPostDto> latestPosts = postService.getLatestPostsByUserId(id);
        latestPosts.forEach(post -> {
            if (!forumDtoFactory.isMember(post.getForumId(), userWhoSearchesId)) {
                post.setContent(null);
            }
        });
        return latestPosts;
    }

    @Override
    public User getUserEntityById(Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        return userOptional.orElseThrow(() -> new NotFoundException("El usuario no fue encontrado"));
    }

    @Override
    public UserDto updateUser(long id, UpdateUserDto updateUserDTO) {
        Optional<User> userOptional = this.userRepository.findById(id);
        User user = userOptional.orElseThrow(() -> new NotFoundException("El usuario no fue encontrado"));
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

    @Override
    public UserDto setUserPrivacy(Long id) {
        User user = getUserEntityById(id);
        user.setPrivacy(!user.isPrivacy());
        return UserDto.from(userRepository.save(user));
    }

    @Override
    public boolean toggleUserForumNotification(Long userId, Long forumId) {
        User user = getUserEntityById(userId);
        boolean isNotificationEnabled;
        if (user.getForumNotifications().contains(forumId)) {
            user.getForumNotifications().remove(forumId);
            isNotificationEnabled = false;
        } else {
            user.getForumNotifications().add(forumId);
            isNotificationEnabled = true;
        }
        userRepository.save(user);
        return isNotificationEnabled;
    }
}
