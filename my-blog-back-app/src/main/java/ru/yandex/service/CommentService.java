package ru.yandex.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.dto.comment.CommentResponse;
import ru.yandex.dto.comment.CreateCommentRequest;
import ru.yandex.dto.comment.UpdateCommentRequest;
import ru.yandex.exception.BadRequestException;
import ru.yandex.repository.CommentRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    /**
     * Получение комментария
     */
    public CommentResponse getCommentByPostIdAndCommentId(Long postId, Long commentId) {
        log.info("Getting comment={}, post={}", commentId, postId);
        return commentRepository.getCommentByPostIdAndCommentId(postId, commentId);
    }

    /**
     * Получение комментариев поста
     */

    public List<CommentResponse> getCommentsByPostId(Long postId) {
        log.info("Getting comments for post={}", postId);
        return commentRepository.getCommentsByPostId(postId);
    }

    /**
     * Создание комментария
     */
    @Transactional
    public CommentResponse createComment(Long postId, CreateCommentRequest request) {
        log.info("Creating comment for post={}", postId);
        return commentRepository.createComment(postId, request);
    }

    /**
     * Обновление комментария
     */
    @Transactional
    public CommentResponse updateComment(Long postId, Long commentId, UpdateCommentRequest request) {
        log.info("Updating comment={}, post={}", commentId, postId);

        if (!request.getId().equals(commentId)) {
            throw new BadRequestException(
                    "Comment id mismatch"
            );
        }

        if (!request.getPostId().equals(postId)) {
            throw new BadRequestException(
                    "Post id mismatch");
        }
        return commentRepository.updateComment(postId, commentId, request);
    }

    /**
     * Удаление комментария
     */
    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        log.info("Deleting comment={}, post={}", commentId, postId);
        commentRepository.deleteComment(postId, commentId);
    }
}
