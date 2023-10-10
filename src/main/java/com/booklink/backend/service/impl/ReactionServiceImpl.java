package com.booklink.backend.service.impl;

import com.booklink.backend.model.Reactable;
import com.booklink.backend.service.ReactionService;
import org.springframework.stereotype.Service;

@Service
public class ReactionServiceImpl<T extends Reactable> implements ReactionService<T> {
    @Override
    public T toggleLike(T entity, Long userId) {
        if (entity.getLikes().contains(userId)) {
            entity.getLikes().remove(userId);
        } else {
            entity.getLikes().add(userId);
        }
        return entity;
    }

    @Override
    public T toggleDislike(T entity, Long userId) {
        entity.getLikes().remove(userId);
        if (entity.getDislikes().contains(userId)) {
            entity.getDislikes().remove(userId);
        } else {
            entity.getDislikes().add(userId);
        }
        return entity;
    }
}
