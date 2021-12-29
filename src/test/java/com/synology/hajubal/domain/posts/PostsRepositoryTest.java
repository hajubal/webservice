package com.synology.hajubal.domain.posts;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PostsRepositoryTest {
    @Autowired
    PostsRepository postsRepository;

    @AfterEach
    public void cleanup() {
        postsRepository.deleteAll();
    }

    @Test
    public void 게시글저장_불러오기() {
        String title = "게시글 제목";
        String contents = "게시글 내용";

        postsRepository.save(Posts.builder()
                .title(title)
                .content(contents)
                .author("tester")
                .build());

        List<Posts> postsList = postsRepository.findAll();

        Posts posts = postsList.get(0);

        assertThat(posts.getTitle()).isEqualTo(title);
        assertThat(posts.getContent()).isEqualTo(contents);
    }

    @Test
    public void BaseTimeEntity_등록() {
        LocalDateTime now = LocalDateTime.of(2019, 6, 4, 0, 0, 0);
        postsRepository.save(Posts.builder()
                        .title("title")
                        .content("content")
                        .author("author")
                        .build()
        );

        List<Posts> postsList = postsRepository.findAll();

        for(Posts post : postsList) {
            System.out.printf(">>>> %s, %s \n", post.getCreatedDate(), post.getModifiedDate());
        }

        assertThat(postsList.get(0).getCreatedDate()).isAfter(now);
        assertThat(postsList.get(0).getModifiedDate()).isAfter(now);
    }
}
