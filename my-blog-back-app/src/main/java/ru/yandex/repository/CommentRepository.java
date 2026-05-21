package ru.yandex.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.dto.comment.CommentResponse;
import ru.yandex.dto.comment.CreateCommentRequest;
import ru.yandex.dto.comment.UpdateCommentRequest;
import ru.yandex.exception.CommentNotFoundException;
import ru.yandex.exception.PostNotFoundException;

import java.util.List;

import static ru.yandex.util.PostSqlQueries.*;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Получение комментариев поста
     */
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        return jdbcTemplate.query(FIND_COMMENTS_BY_POST_ID, (rs, rowNum) ->
                        CommentResponse.builder()
                                .id(rs.getLong("id"))
                                .text(rs.getString("text"))
                                .postId(rs.getLong("post_id"))
                                .build(),
                postId);
    }

    /**
     * Получение комментария поста
     */
    public CommentResponse getCommentByPostIdAndCommentId(Long postId, Long commentId) {
        try {
            return jdbcTemplate.queryForObject(FIND_COMMENT_BY_ID_AND_POST_ID, (rs, row) ->
                            CommentResponse.builder()
                                    .id(rs.getLong("id"))
                                    .text(rs.getString("text"))
                                    .postId(rs.getLong("post_id"))
                                    .build(),
                    postId,
                    commentId);
        } catch (EmptyResultDataAccessException e) {
            throw new CommentNotFoundException("Comment not found", e, commentId);
        }
    }

    /**
     * Добавление комментария к посту
     */
    public CommentResponse createComment(Long postId, CreateCommentRequest request) {
        checkPostExists(postId);
        return jdbcTemplate.queryForObject(
                INSERT_COMMENT,
                (rs, rowNum) -> new CommentResponse(
                        rs.getLong("id"),
                        rs.getString("text"),
                        rs.getLong("post_id")
                ),
                request.getText(),
                postId
        );
    }

    /**
     * Редактирование комментария к посту
     */
    public CommentResponse updateComment(Long postId, Long commentId, UpdateCommentRequest comment) {
        checkPostExists(postId);
        int updated = jdbcTemplate.update(UPDATE_COMMENT, comment.getText(), commentId, postId);

        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Comment not found");
        }
        return CommentResponse.builder()
                .id(commentId)
                .text(comment.getText())
                .postId(postId)
                .build();
    }

    /**
     * Удаление комментария
     */
    public void deleteComment(Long postId, Long commentId) {
        jdbcTemplate.update(DELETE_COMMENT, commentId, postId);
    }

    private void checkPostExists(Long postId) {
        Boolean exists = jdbcTemplate.queryForObject("""
                            SELECT EXISTS(SELECT 1 FROM posts WHERE id = ?)
                        """, Boolean.class,
                postId);
        if (!exists) {
            throw new PostNotFoundException("Post not found with id: " + postId, postId);
        }
    }
}
