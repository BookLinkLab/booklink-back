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

    @Column(name = "post_author_id" ,nullable = false)
    private Long postAuthorId;
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_author_id", insertable = false, updatable = false)
    private User postAuthor;

    @Column(name = "comment_author_id", nullable = true)
    private Long commentAuthorId;
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_author_id", insertable = false, updatable = false)
    private User commentAuthor;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", insertable = false, updatable = false)
    private User receiver;

    @Column(name = "forum_id", nullable = false)
    private Long forumId;
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id", insertable = false, updatable = false)
    private Forum forum;

    @Column(name = "post_id", nullable = false)
    private Long postId;
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private Post post;

    @Column(name = "comment_id", nullable = true)
    private Long commentId;
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", insertable = false, updatable = false)
    private Comment comment;

    @Column(nullable = false)
    private Date createdDate;
}
