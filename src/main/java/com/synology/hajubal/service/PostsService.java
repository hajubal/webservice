package com.synology.hajubal.service;

import com.synology.hajubal.domain.posts.Posts;
import com.synology.hajubal.domain.posts.PostsRepository;
import com.synology.hajubal.web.dto.PostsListResponseDto;
import com.synology.hajubal.web.dto.PostsResponseDto;
import com.synology.hajubal.web.dto.PostsSaveRequestDto;
import com.synology.hajubal.web.dto.PostsUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostsService {
    private final PostsRepository postsRepository;

    public Long save(PostsSaveRequestDto requestDto) {
        return postsRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional
    public Long update(Long id, PostsUpdateRequestDto requestDto) {
        Posts posts = postsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("no content id: " + id));

        posts.update(requestDto.getTitle(), requestDto.getContent());

        //@Transactional 를 추가해줬기 때문에 엔티티의 영속성이 유지됨. @Transactional 를 추가하지 않으면 update 문을 통해 수정해줘야 함.
        //postsRepository.save(posts);

        return id;
    }

    public PostsResponseDto findById(Long id) {
        Posts entity = postsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("no content id: " + id));

        return new PostsResponseDto(entity);
    }

    @Transactional(readOnly = true)
    public List<PostsListResponseDto> findAllDesc() {
        return postsRepository.findAllDesc().stream().map(PostsListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        Posts posts = postsRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));

        postsRepository.delete(posts);
    }
}
