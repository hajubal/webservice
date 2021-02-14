package com.synology.hajubal.web.dto;

import com.synology.hajubal.domain.posts.Posts;
import lombok.Getter;

@Getter
public class PostsResponseDto {

    private String title;
    private String content;
    private Long id;
    private String author;

    public PostsResponseDto(Posts entity) {
        this.title = entity.getTitle();
        this.id = entity.getId();
        this.content = entity.getContent();
        this.author = entity.getAuthor();
    }
}
