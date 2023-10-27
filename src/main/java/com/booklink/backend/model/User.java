package com.booklink.backend.model;

import com.booklink.backend.dto.user.CreateUserDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Column(nullable = false)
    private boolean privacy = false;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Forum> forumsCreated;

    @ToString.Exclude
    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
    private List<Forum> forumsJoined;

    @ElementCollection
    private List<Long> forumNotifications;

    public static User from(CreateUserDto userDto, String encryptedPassword) {
        return User.builder()
                .email(userDto.getEmail())
                .username(userDto.getUsername())
                .password(encryptedPassword)
                .forumNotifications(List.of())
                .build();
    }
}
