package com.synology.hajubal.service;

import com.synology.hajubal.domain.posts.Posts;
import com.synology.hajubal.domain.posts.PostsRepository;
import com.synology.hajubal.web.dto.PostsResponseDto;
import com.synology.hajubal.web.dto.PostsSaveRequestDto;
import com.synology.hajubal.web.dto.PostsUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostsService {
    private final PostsRepository postsRepository;

    public Long save(PostsSaveRequestDto requestDto) {
        return postsRepository.save(requestDto.toEntity()).getId();
    }

    public Long update(Long id, PostsUpdateRequestDto requestDto) {
        Posts posts = postsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("no content id: " + id));

        posts.update(requestDto.getTitle(), requestDto.getContent());

        return id;
    }

    public PostsResponseDto findById(Long id) {
        Posts entity = postsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("no content id: " + id));

        return new PostsResponseDto(entity);
    }
}
