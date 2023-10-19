package com.booklink.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private NotificationType type;

    @Column(nullable = false)
    private Long postAuthorId;

    @Column(nullable = true)
    private Long commentAuthorId;

    @Column(nullable = false)
    private Long receiverId;

    @Column(nullable = false)
    private Long forumId;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = true)
    private Long commentId;

    @Column(nullable = false)
    private Date createdDate;
}
