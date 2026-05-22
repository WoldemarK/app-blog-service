package ru.yandex.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PostSqlQueries {

    public static final String IND_POST_BY_ID = """
                  SELECT p.id,
                   p.title,
                   p.text,
                   p.likes_count,
                   COALESCE(
                       ARRAY_AGG(t.name) FILTER (WHERE t.name IS NOT NULL),
                       ARRAY[]::text[]
                   ) AS tags,
                   (
                       SELECT COUNT(*)
                       FROM comments c
                       WHERE c.post_id = p.id
                   ) AS comments_count
            FROM posts p
            LEFT JOIN post_tags pt ON p.id = pt.post_id
            LEFT JOIN tags t ON pt.tag_id = t.id
            WHERE p.id = ?
            GROUP BY p.id, p.title, p.text, p.likes_count
            """;

    public static final String INSERT_POST_AND_RETURN_ID = """
            INSERT INTO posts(title, text)
            values (?,?)
            returning id
            """;

    public static final String INSERT_POST_TAG = """
            INSERT INTO post_tags(post_id, tag_id)
            VALUES (?, ?)
            """;

    public static final String BATCH_UPDATE = """
            INSERT INTO tags(name)
            VALUES (?)
            ON CONFLICT(name) DO NOTHING
            """;

    public static final String FIND_TAGS_BY_NAMES = """
            SELECT id, name
            FROM tags
            WHERE name IN (%s)
            """;

    public static final String UPDATE_POST = """
                UPDATE posts
                SET title = ?, text = ?
                WHERE id = ?
            """;

    public static final String BATCH_UPDATE_TAGS = """
                INSERT INTO tags(name)
                VALUES (?)
                ON CONFLICT(name)
                DO NOTHING
            """;

    public static final String DELETE_POST_TAGS = """
                DELETE FROM post_tags
                WHERE post_id = ?
            """;

    public static final String FIND_POSTS_PAGINATED = """
                SELECT p.id,
                       p.title,
                       p.text,
                       p.likes_count,
                       COALESCE(ARRAY_AGG(t.name) FILTER (WHERE t.name IS NOT NULL), ARRAY[]::text[]) AS tags,
                       (SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) AS comments_count
                FROM posts p
                LEFT JOIN post_tags pt ON p.id = pt.post_id
                LEFT JOIN tags t ON pt.tag_id = t.id
                WHERE p.title ILIKE ? OR p.text ILIKE ?
                GROUP BY p.id
                ORDER BY p.id DESC
                LIMIT ? OFFSET ?
            """;

    public static final String COUNT_POSTS = """
                SELECT COUNT(*)
                FROM posts
                WHERE title
                ILIKE ?
                OR text
                ILIKE ?
            """;

    public static final String INCREMENT_LIKES = """
            UPDATE posts
            SET likes_count = likes_count + 1
            WHERE id = ?
            RETURNING likes_count
            """;

    public static final String DELETE_POSTS_BY_ID = """
                        DELETE FROM posts
                        WHERE id = ?
            """;

    public static final String UPDATE_POST_IMAGE = """
            UPDATE posts
            SET image = ?
            WHERE id = ?
            """;
    public static final String FIND_IMAGE_PATH = """
            SELECT image
            FROM posts
            WHERE id = ?
            """;
    public static final String FIND_COMMENT_BY_ID_AND_POST_ID = """
             SELECT c.id, c.text, c.post_id
             FROM comments c
             WHERE c.post_id = ?
             AND c.id = ?
            """;

    public static final String FIND_COMMENTS_BY_POST_ID = """
            SELECT id, text, post_id
            FROM comments
            WHERE post_id = ?
            """;

    public static final String INSERT_COMMENT = """
            INSERT INTO comments(text, post_id)
            VALUES (?, ?)
            RETURNING id, text, post_id
            """;

    public static final String UPDATE_COMMENT = """
            UPDATE comments
            SET text = ?
            WHERE id = ?
              AND post_id = ?
            """;
    public static final String DELETE_COMMENT = """
        DELETE FROM comments
        WHERE id = ?
          AND post_id = ?
        """;
}
