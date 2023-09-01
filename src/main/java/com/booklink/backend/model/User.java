package com.booklink.backend.model;

import com.booklink.backend.dto.user.CreateUserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Forum> forumsCreated;

    @ManyToMany(mappedBy = "members", fetch = FetchType.EAGER)
    private List<Forum> forumsJoined;

    public static User from(CreateUserDto userDto, String encryptedPassword) {
        return User.builder()
                .email(userDto.getEmail())
                .username(userDto.getUsername())
                .password(encryptedPassword)
                .build();
    }
}
