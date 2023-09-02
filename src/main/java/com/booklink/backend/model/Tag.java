package com.booklink.backend.model;

import com.booklink.backend.dto.tag.CreateTagDto;
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
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "tags")
    private List<Forum> forums;

    public static Tag from(CreateTagDto createTagDto) {
        return Tag.builder()
                .name(createTagDto.getName())
                .build();
    }

}
