package ru.yandex.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.dto.comment.CommentResponse;
import ru.yandex.dto.comment.CreateCommentRequest;
import ru.yandex.dto.comment.UpdateCommentRequest;
import ru.yandex.dto.post.CreatePostRequest;
import ru.yandex.dto.post.PostDto;
import ru.yandex.dto.post.PostsResponse;
import ru.yandex.dto.post.UpdatePostRequest;
import ru.yandex.service.CommentService;
import ru.yandex.service.FileStorageService;
import ru.yandex.service.PostService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final FileStorageService fileStorageService;
    private final CommentService commentService;


    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody CreatePostRequest request) {
        log.debug("Create post request: {}", request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postService.createPost(request));
    }

    @PostMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable("id") Long id) {
        log.debug("Get post by id: {}", id);
        return new ResponseEntity<>(postService.findPostById(id),
                HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(@PathVariable("id") Long id,
                                              @RequestBody UpdatePostRequest request) {
        log.debug("Update post request: {}", request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(postService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long id) {
        log.debug("Delete post by id: {}", id);
        postService.deletePost(id);
        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping
    public ResponseEntity<PostsResponse> getPosts(@RequestParam(value = "search", defaultValue = "") String search,
                                                  @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                                                  @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {
        log.debug("Get posts by search: {}", search);
        PostsResponse page = postService.getPosts(search, pageNumber, pageSize);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity<Integer> incrementLikes(@PathVariable("id") Long id) {
        log.debug("Increment likes: {}", id);
        return ResponseEntity.ok(postService.incrementLikes(id));
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Void> updatePostImage(@PathVariable("id") Long id,
                                                @RequestParam("image") MultipartFile image) {
        log.debug("Update post image: {}", image);
        String imagePath = fileStorageService.saveImage(image);
        fileStorageService.updatePostImage(id, imagePath);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getPostImage(@PathVariable("id") Long id) {
        log.debug("Get post image: {}", id);
        byte[] image = fileStorageService.getPostImage(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }

    @GetMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> getCommentByPostAndCommentId(@PathVariable("postId") Long postId,
                                                                @PathVariable("commentId") Long commentId) {
        log.debug("Get comment by post and comment id: {}", commentId);
        return ResponseEntity.ok(commentService.getCommentByPostIdAndCommentId(postId, commentId));

    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsByPostId(@PathVariable("id") Long id) {
        log.debug("Get comments by post id: {}", id);
        return ResponseEntity.ok(commentService.getCommentsByPostId(id));

    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponse> createComment(@PathVariable("id") Long postId,
                                                         @RequestBody CreateCommentRequest request) {
        CommentResponse response = commentService.createComment(postId, request);
        log.debug("Create comment response: {}", response);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable("postId") Long postId,
                                                 @PathVariable("commentId") Long commentId,
                                                 @RequestBody UpdateCommentRequest comment) {
        log.debug("Update comment request: {}", comment);
        return ResponseEntity.ok(commentService.updateComment(postId, commentId, comment));
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("postId") Long postId,
                                              @PathVariable("commentId") Long commentId) {
        log.debug("Delete comment request: {}", commentId);
        commentService.deleteComment(postId, commentId);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }
}
