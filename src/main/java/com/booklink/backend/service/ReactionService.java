package com.booklink.backend.service;

public interface ReactionService<T> {
    T toggleLike(T entity, Long userId);
    T toggleDislike(T entity, Long userId);
}
