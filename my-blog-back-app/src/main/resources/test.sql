explain analyze
SELECT distinct p.id,
                p.title,
                p.text,
                p.likes_count,
                COALESCE(ARRAY_AGG(t.name), ARRAY []::text[])            AS tags,
                (SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) AS comments_count
FROM posts p
         LEFT JOIN post_tags pt ON p.id = pt.post_id
         LEFT JOIN tags t ON pt.tag_id = t.id
WHERE p.id = 3
GROUP BY p.id, p.title, p.text, p.likes_count;

SELECT *
FROM posts
ORDER BY id;

SELECT pg_get_serial_sequence('posts', 'id');

SELECT setval(
               pg_get_serial_sequence('posts', 'id'),
               (SELECT COALESCE(MAX(id), 1) FROM posts)
       );

TRUNCATE TABLE posts RESTART IDENTITY CASCADE;

select *
from posts p
         join comments c on p.id = c.post_id
where p.id = 1;


select c.id, c.text, c.post_id
from comments c
         join posts p on c.post_id = p.id
where p.id = 1;