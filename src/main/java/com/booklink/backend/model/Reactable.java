package com.booklink.backend.model;

import java.util.List;

public interface Reactable {
    List<Long> getLikes();
    List<Long> getDislikes();
}
