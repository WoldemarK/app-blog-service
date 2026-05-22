package ru.yandex.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.dto.comment.CommentResponse;
import ru.yandex.dto.comment.CreateCommentRequest;
import ru.yandex.dto.post.*;
import ru.yandex.service.*;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private PostController postController;

    @Mock
    private PostService postService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private CommentService commentService;

    @Mock
    private InMemoryIdempotencyService idempotencyService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
    }

    @Test
    void shouldCreatePost() throws Exception {
        Long id = 1L;
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("title");
        request.setText("title");
        request.setTags(List.of("tag1", "tag2"));

        PostDto response = PostDto.builder()
                .id(id)
                .title("Java")
                .text("Spring Boot")
                .tags(List.of("java", "spring"))
                .likesCount(0)
                .commentsCount(0)
                .build();
        when(idempotencyService.execute(eq("123"), any())).thenReturn(response);

        mockMvc.perform(post("/api/posts")
                        .header("key", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Java"))
                .andExpect(jsonPath("$.text").value("Spring Boot"))
                .andExpect(jsonPath("$.tags[0]").value("java"))
                .andExpect(jsonPath("$.likesCount").value(0))
                .andExpect(jsonPath("$.commentsCount").value(0));

        verify(idempotencyService, times(1)).execute(eq("123"), any());
    }

    @Test
    void shouldGetPostById() throws Exception {
        Long id = 1L;
        PostDto response = PostDto.builder()
                .id(1L)
                .title("Post")
                .text("Text")
                .tags(List.of("java"))
                .likesCount(10)
                .commentsCount(5)
                .build();
        when(postService.findPostById(id)).thenReturn(response);
        mockMvc.perform(get("/api/posts/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Post"))
                .andExpect(jsonPath("$.text").value("Text"))
                .andExpect(jsonPath("$.likesCount").value(10))
                .andExpect(jsonPath("$.commentsCount").value(5))
                .andExpect(jsonPath("$.tags[0]").value("java"))
                .andExpect(jsonPath("$.likesCount").value(10))
                .andExpect(jsonPath("$.commentsCount").value(5));

    }

    @Test
    void shouldGetPosts() throws Exception {
        Long id = 1L;
        PostPreviewDto preview = PostPreviewDto.builder()
                .id(id)
                .title("Preview")
                .text("Preview text")
                .tags(List.of("spring"))
                .likesCount(2)
                .commentsCount(1)
                .build();

        PostsResponse response = PostsResponse.builder()
                .hasPrev(false)
                .hasNext(false)
                .lastPage(1)
                .posts(List.of(preview))
                .build();
        when(postService.getPosts("", 1, 5)).thenReturn(response);

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasPrev").value(false))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.lastPage").value(1))
                .andExpect(jsonPath("$.posts[0].id").value(1))
                .andExpect(jsonPath("$.posts[0].title").value("Preview"));
    }
    @Test
    void shouldIncrementLikes() throws Exception {
        Long id = 1L;
        when(postService.incrementLikes(id)).thenReturn(15);

        mockMvc.perform(post("/api/posts/1/likes"))
                .andExpect(status().isOk())
                .andExpect(content().string("15"));
    }

    @Test
    void shouldUploadImage() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "image.jpg",
                "image/jpeg",
                "image".getBytes()
        );

        when(fileStorageService.saveImage(any())).thenReturn("image-path");

        mockMvc.perform(multipart("/api/posts/1/image")
                        .file(file)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        }))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetImage() throws Exception {
        Long id = 1L;
        byte[] image = "image".getBytes();

        when(fileStorageService.getPostImage(id)).thenReturn(image);

        mockMvc.perform(get("/api/posts/1/image"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(image));
    }

    @Test
    void shouldReturn404WhenImageNotFound() throws Exception {
        Long id = 1L;
        when(fileStorageService.getPostImage(id)).thenReturn(new byte[0]);

        mockMvc.perform(get("/api/posts/1/image"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateComment() throws Exception {
        Long id = 1L;
        CreateCommentRequest request = CreateCommentRequest.builder()
                .text("comment")
                .postId(id)
                .build();

        CommentResponse response = CommentResponse.builder()
                .id(1L)
                .text("comment")
                .postId(1L)
                .build();

        when(commentService.createComment(eq(id), any())).thenReturn(response);

        mockMvc.perform(post("/api/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("comment"))
                .andExpect(jsonPath("$.postId").value(1));
    }

    @Test
    void shouldGetCommentsByPostId() throws Exception {
        Long id = 1L;
        CommentResponse comment = CommentResponse.builder()
                .id(id)
                .text("comment")
                .postId(id)
                .build();

        when(commentService.getCommentsByPostId(id)).thenReturn(List.of(comment));

        mockMvc.perform(get("/api/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].text").value("comment"))
                .andExpect(jsonPath("$[0].postId").value(1));
    }

    @Test
    void shouldGetCommentById() throws Exception {
        Long commentId = 1L;
        Long postId = 1L;
        CommentResponse comment = CommentResponse.builder()
                .id(commentId)
                .text("comment")
                .postId(postId)
                .build();

        when(commentService.getCommentByPostIdAndCommentId(postId, commentId))
                .thenReturn(comment);

        mockMvc.perform(get("/api/posts/1/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("comment"))
                .andExpect(jsonPath("$.postId").value(1));
    }
}