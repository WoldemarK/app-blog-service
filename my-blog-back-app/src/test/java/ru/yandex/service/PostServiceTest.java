package ru.yandex.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.dto.post.CreatePostRequest;
import ru.yandex.dto.post.PostDto;
import ru.yandex.dto.post.PostsResponse;
import ru.yandex.dto.post.UpdatePostRequest;
import ru.yandex.repository.PostRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void shouldFindPostById() {

        PostDto dto = PostDto.builder()
                .id(1L)
                .title("title")
                .build();

        when(postRepository.findById(1L)).thenReturn(dto);

        PostDto result = postService.findPostById(1L);

        assertEquals(1L, result.getId());
        assertEquals("title", result.getTitle());

        verify(postRepository).findById(1L);
    }

    @Test
    void shouldCreatePost() {

        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("new");

        PostDto dto = PostDto.builder()
                .id(1L)
                .title("new")
                .build();

        when(postRepository.createNewPost(request)).thenReturn(dto);

        PostDto result = postService.createPost(request);

        assertEquals("new", result.getTitle());

        verify(postRepository).createNewPost(request);
    }

    @Test
    void shouldUpdatePost() {

        UpdatePostRequest request = new UpdatePostRequest();
        request.setTitle("updated");

        PostDto dto = PostDto.builder()
                .id(1L)
                .title("updated")
                .build();

        when(postRepository.updatePost(1L, request)).thenReturn(dto);

        PostDto result = postService.update(1L, request);

        assertEquals("updated", result.getTitle());

        verify(postRepository).updatePost(1L, request);
    }

    @Test
    void shouldDeletePost() {

        doNothing().when(postRepository).deletePostById(1L);

        postService.deletePost(1L);

        verify(postRepository).deletePostById(1L);
    }

    @Test
    void shouldGetPosts() {

        PostsResponse response = new PostsResponse();

        when(postRepository.getPosts("", 1, 5)).thenReturn(response);

        PostsResponse result = postService.getPosts("", 1, 5);

        assertEquals(response, result);

        verify(postRepository).getPosts("", 1, 5);
    }

    @Test
    void shouldIncrementLikes() {

        when(postRepository.incrementLikes(1L)).thenReturn(10);

        Integer result = postService.incrementLikes(1L);

        assertEquals(10, result);

        verify(postRepository).incrementLikes(1L);
    }

    @Test
    void shouldThrowException_whenPostIdIsNull() {

        assertThrows(IllegalArgumentException.class,() -> postService.incrementLikes(null));

        verify(postRepository, never()).incrementLikes(any());
    }
}