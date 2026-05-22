package ru.yandex.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import ru.yandex.exception.PostNotFoundException;

import static ru.yandex.util.PostSqlQueries.FIND_IMAGE_PATH;
import static ru.yandex.util.PostSqlQueries.UPDATE_POST_IMAGE;

@Repository
@RequiredArgsConstructor
public class FileStorageRepository {

    private final JdbcTemplate jdbcTemplate;


    /**
     * Обновление картинки поста
     */
    public void updatePostImage(Long postId, String imagePath) {
        int updated = jdbcTemplate.update(UPDATE_POST_IMAGE, imagePath, postId);

        if (updated == 0) {
            throw new PostNotFoundException("Post not found with id: " + postId, postId);
        }
    }

    /**
     * Получение картинки поста
     */
    public String getImagePathByPostId(Long postId) {
        try {
            String imagePath = jdbcTemplate.queryForObject(
                    FIND_IMAGE_PATH,
                    String.class,
                    postId
            );
            if (!StringUtils.hasText(imagePath)) {
                return null;
            }
            return imagePath;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

}
