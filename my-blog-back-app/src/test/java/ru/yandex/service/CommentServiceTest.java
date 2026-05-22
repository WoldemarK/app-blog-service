package ru.yandex.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.dto.comment.CommentResponse;
import ru.yandex.dto.comment.CreateCommentRequest;
import ru.yandex.dto.comment.UpdateCommentRequest;
import ru.yandex.exception.BadRequestException;
import ru.yandex.repository.CommentRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void shouldGetCommentById() {

        CommentResponse response = new CommentResponse(1L, "text", 1L);

        when(commentRepository.getCommentByPostIdAndCommentId(1L, 1L)).thenReturn(response);

        CommentResponse result = commentService.getCommentByPostIdAndCommentId(1L, 1L);

        assertEquals("text", result.getText());
        assertEquals(1L, result.getPostId());

        verify(commentRepository).getCommentByPostIdAndCommentId(1L, 1L);
    }

    @Test
    void shouldGetCommentsByPostId() {

        List<CommentResponse> list = List.of(new CommentResponse(1L, "text", 1L));

        when(commentRepository.getCommentsByPostId(1L)).thenReturn(list);

        List<CommentResponse> result = commentService.getCommentsByPostId(1L);

        assertEquals(1, result.size());
        assertEquals("text", result.get(0).getText());

        verify(commentRepository).getCommentsByPostId(1L);
    }

    @Test
    void shouldCreateComment() {
        CreateCommentRequest request = new CreateCommentRequest("hello", 1L);
        CommentResponse response = new CommentResponse(1L, "hello", 1L);

        when(commentRepository.createComment(1L, request)).thenReturn(response);

        CommentResponse result = commentService.createComment(1L, request);

        assertEquals("hello", result.getText());

        verify(commentRepository).createComment(1L, request);
    }

    @Test
    void shouldUpdateComment_whenValidIds() {
        UpdateCommentRequest request = new UpdateCommentRequest(1L, "new", 1L);

        CommentResponse response = new CommentResponse(1L, "new", 1L);

        when(commentRepository.updateComment(1L, 1L, request)).thenReturn(response);

        CommentResponse result = commentService.updateComment(1L, 1L, request);

        assertEquals("new", result.getText());

        verify(commentRepository).updateComment(1L, 1L, request);
    }

    @Test
    void shouldThrowException_whenCommentIdMismatch() {
        UpdateCommentRequest request = new UpdateCommentRequest(2L, "new", 1L);

        assertThrows(BadRequestException.class, () -> commentService.updateComment(1L, 1L, request));

        verify(commentRepository, never()).updateComment(any(), any(), any());
    }

    @Test
    void shouldThrowException_whenPostIdMismatch() {
        UpdateCommentRequest request = new UpdateCommentRequest(1L, "new", 2L);

        assertThrows(BadRequestException.class, () -> commentService.updateComment(1L, 1L, request));

        verify(commentRepository, never()).updateComment(any(), any(), any());
    }

    @Test
    void shouldDeleteComment() {
        doNothing().when(commentRepository).deleteComment(1L, 1L);

        commentService.deleteComment(1L, 1L);

        verify(commentRepository).deleteComment(1L, 1L);
    }
}