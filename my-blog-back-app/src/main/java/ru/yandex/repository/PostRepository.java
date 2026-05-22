package ru.yandex.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.dto.post.*;
import ru.yandex.exception.PostNotFoundException;

import java.text.MessageFormat;
import java.util.*;

import static ru.yandex.util.PostSqlQueries.*;


@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Создание поста
     */
    @Transactional
    public PostDto createNewPost(CreatePostRequest request) {

        Set<String> tagNames = new LinkedHashSet<>(request.getTags());

        Long postId = jdbcTemplate.queryForObject(
                INSERT_POST_AND_RETURN_ID,
                Long.class,
                request.getTitle(),
                request.getText()
        );

        if (!tagNames.isEmpty()) {

            jdbcTemplate.batchUpdate(
                    BATCH_UPDATE,
                    tagNames,
                    100,
                    (ps, tag) -> ps.setString(1, tag)
            );

            String placeholders = String.join(",", Collections.nCopies(tagNames.size(), "?"));
            String sql = FIND_TAGS_BY_NAMES.formatted(placeholders);

            Map<String, Long> tagMap = new HashMap<>();

            jdbcTemplate.query(sql, tagNames.toArray(), rs -> {
                tagMap.put(rs.getString("name"), rs.getLong("id"));
            });

            jdbcTemplate.batchUpdate(
                    INSERT_POST_TAG,
                    tagNames,
                    100,
                    (ps, tag) -> {
                        ps.setLong(1, postId);
                        ps.setLong(2, tagMap.get(tag));
                    }
            );
        }

        return PostDto.builder()
                .id(postId)
                .title(request.getTitle())
                .text(request.getText())
                .tags(new ArrayList<>(tagNames))
                .likesCount(0)
                .commentsCount(0)
                .build();
    }

    /**
     * Получение поста по id
     */
    public PostDto findById(Long postId) {
        try {
            return jdbcTemplate.queryForObject(
                    IND_POST_BY_ID,
                    (rs, rowNum) -> {

                        String[] tagsArray = Optional.ofNullable(rs.getArray("tags"))
                                .map(arr -> {
                                    try {
                                        return (String[]) arr.getArray();
                                    } catch (Exception e) {
                                        return new String[0];
                                    }
                                })
                                .orElse(new String[0]);

                        return PostDto.builder()
                                .id(rs.getLong("id"))
                                .title(rs.getString("title"))
                                .text(rs.getString("text"))
                                .likesCount(rs.getInt("likes_count"))
                                .commentsCount(rs.getInt("comments_count"))
                                .tags(Arrays.asList(tagsArray))
                                .build();
                    },
                    postId
            );
        }catch (EmptyResultDataAccessException e){
            throw new PostNotFoundException("Post not found with id: " + postId, postId);
        }
    }

    /**
     * Обновление поста
     */
    @Transactional
    public PostDto updatePost(Long postId, UpdatePostRequest request) {

        jdbcTemplate.update(
                UPDATE_POST,
                request.getTitle(),
                request.getText(),
                postId
        );

        Set<String> tagNames = new LinkedHashSet<>(request.getTags());

        jdbcTemplate.update(DELETE_POST_TAGS, postId);

        if (!tagNames.isEmpty()) {

            jdbcTemplate.batchUpdate(
                    BATCH_UPDATE_TAGS,
                    tagNames,
                    100,
                    (ps, tag) -> ps.setString(1, tag)
            );

            String placeholders = String.join(",", Collections.nCopies(tagNames.size(), "?"));
            String sql = FIND_TAGS_BY_NAMES.formatted(placeholders);

            Map<String, Long> tagMap = new HashMap<>();

            jdbcTemplate.query(sql, tagNames.toArray(), rs -> {
                tagMap.put(rs.getString("name"), rs.getLong("id"));
            });

            jdbcTemplate.batchUpdate(
                    INSERT_POST_TAG,
                    tagNames,
                    100,
                    (ps, tag) -> {
                        ps.setLong(1, postId);
                        ps.setLong(2, tagMap.get(tag));
                    }
            );
        }

        return findById(postId);
    }

    /**
     * Удаление поста
     */
    public void deletePostById(Long postId) {

        int rows = jdbcTemplate.update(DELETE_POSTS_BY_ID, postId);

        if (rows == 0) {
            throw new PostNotFoundException(
                    MessageFormat.format("Post not found with id: {0}", postId),
                    postId);
        }
    }

    /**
     * Получение постов (пагинация + поиск)
     */
    public PostsResponse getPosts(String search, int pageNumber, int pageSize) {

        String searchPattern = "%" + search + "%";

        Integer totalPosts = jdbcTemplate.queryForObject(
                COUNT_POSTS,
                Integer.class,
                searchPattern,
                searchPattern
        );

        int lastPage = (int) Math.ceil((double) totalPosts / pageSize);
        lastPage = Math.max(lastPage, 1);

        boolean hasPrev = pageNumber > 1;
        boolean hasNext = pageNumber < lastPage;

        int offset = (pageNumber - 1) * pageSize;

        List<PostPreviewDto> posts = jdbcTemplate.query(
                FIND_POSTS_PAGINATED,
                (rs, rowNum) -> {

                    String text = rs.getString("text");
                    if (text != null && text.length() > 128) {
                        text = text.substring(0, 128) + "…";
                    }

                    String[] tagsArray = Optional.ofNullable(rs.getArray("tags"))
                            .map(arr -> {
                                try {
                                    return (String[]) arr.getArray();
                                } catch (Exception e) {
                                    return new String[0];
                                }
                            })
                            .orElse(new String[0]);

                    return PostPreviewDto.builder()
                            .id(rs.getLong("id"))
                            .title(rs.getString("title"))
                            .text(text)
                            .tags(Arrays.asList(tagsArray))
                            .likesCount(rs.getInt("likes_count"))
                            .commentsCount(rs.getInt("comments_count"))
                            .build();
                },
                searchPattern,
                searchPattern,
                pageSize,
                offset
        );

        return PostsResponse.builder()
                .posts(posts)
                .hasPrev(hasPrev)
                .hasNext(hasNext)
                .lastPage(lastPage)
                .build();
    }

    /**
     * Лайки
     */
    public Integer incrementLikes(Long postId) {
        return jdbcTemplate.queryForObject(
                INCREMENT_LIKES,
                Integer.class,
                postId);
    }
}

