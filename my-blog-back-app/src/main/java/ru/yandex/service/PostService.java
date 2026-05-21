package ru.yandex.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.dto.post.CreatePostRequest;
import ru.yandex.dto.post.PostDto;
import ru.yandex.dto.post.PostsResponse;
import ru.yandex.dto.post.UpdatePostRequest;
import ru.yandex.repository.PostRepository;


@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public PostDto findPostById(Long postId) {
        log.info("Fetching post with id={}", postId);

        PostDto post = postRepository.findById(postId);

        log.info("Post with id={} fetched successfully", postId);
        return post;
    }

    @Transactional
    public PostDto createPost(CreatePostRequest request) {
        log.info("Creating post with title={}", request.getTitle());

        PostDto post = postRepository.createNewPost(request);

        log.info("Post created with id={}", post.getId());
        return post;
    }

    @Transactional
    public PostDto update(Long postId, UpdatePostRequest request) {
        log.info("Updating post with id={}", postId);

        PostDto updated = postRepository.updatePost(postId, request);

        log.info("Post updated with id={}", postId);
        return updated;
    }

    @Transactional
    public void deletePost(Long postId) {
        log.info("Deleting post with id={}", postId);

        postRepository.deletePostById(postId);

        log.info("Post deleted with id={}", postId);
    }

    @Transactional(readOnly = true)
    public PostsResponse getPosts(String search, int pageNumber, int pageSize) {
        log.info("Fetching posts: search={}, page={}, size={}",
                search, pageNumber, pageSize);

        return postRepository.getPosts(search, pageNumber, pageSize);
    }

    @Transactional
    public Integer incrementLikes(Long postId) {

        if (postId == null) {
            throw new IllegalArgumentException("postId must not be null");
        }

        log.info("Incrementing likes for post id={}", postId);

        Integer likes = postRepository.incrementLikes(postId);

        log.info("Likes incremented for post id={}", postId);

        return likes;
    }
}
