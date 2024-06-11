CREATE TABLE users
(
    id                 bigserial PRIMARY KEY,
    creation_date      timestamp    NOT NULL DEFAULT NOW(),
    deletion_date      timestamp    NULL     DEFAULT NULL,
    is_deleted         bool         NOT NULL DEFAULT FALSE,
    last_name          varchar(50)  NOT NULL,
    first_name         varchar(50)  NOT NULL,
    birth_date         timestamp    NOT NULL,
    user_name          varchar(50)  NOT NULL UNIQUE,
    email              varchar(320) NOT NULL,
    email_normalized   varchar(320) NOT NULL GENERATED ALWAYS AS ( LOWER(email) ) STORED,
    password_hash      char(60)     NOT NULL,
    is_email_confirmed bool         NOT NULL DEFAULT FALSE,
    last_login_date    timestamp    NOT NULL DEFAULT NOW()
);

CREATE TABLE roles
(
    id            bigserial PRIMARY KEY,
    creation_date timestamp    NOT NULL DEFAULT NOW(),
    deletion_date timestamp    NULL     DEFAULT NULL,
    is_deleted    bool         NOT NULL DEFAULT FALSE,
    name          varchar(50)  NOT NULL,
    description   varchar(255) NOT NULL,
    permissions   bigint       NOT NULL DEFAULT 0,
    is_default    bool         NOT NULL DEFAULT FALSE
);

CREATE TABLE groups
(
    id            bigserial PRIMARY KEY,
    creation_date timestamp    NOT NULL DEFAULT NOW(),
    deletion_date timestamp    NULL     DEFAULT NULL,
    is_deleted    bool         NOT NULL DEFAULT FALSE,
    name          varchar(50)  NOT NULL,
    description   varchar(255) NOT NULL DEFAULT '',
    year          int          NOT NULL DEFAULT EXTRACT(YEAR FROM NOW())
);

CREATE TABLE posts
(
    id            bigserial PRIMARY KEY,
    creation_date timestamp NOT NULL DEFAULT NOW(),
    deletion_date timestamp NULL     DEFAULT NULL,
    is_deleted    bool      NOT NULL DEFAULT FALSE,
    text          text      NOT NULL,
    user_id       bigint    NOT NULL REFERENCES users (id),
    is_article    boolean   NOT NULL DEFAULT FALSE,
    likes         int       NOT NULL DEFAULT 0,
    comments      int       NOT NULL DEFAULT 0
);

CREATE TABLE comments
(
    id            bigserial PRIMARY KEY,
    creation_date timestamp NOT NULL DEFAULT NOW(),
    deletion_date timestamp NULL     DEFAULT NULL,
    is_deleted    bool      NOT NULL DEFAULT FALSE,
    text          text      NOT NULL,
    user_id       bigint    NOT NULL REFERENCES users (id),
    post_id       bigint    NOT NULL REFERENCES posts (id),
    likes         int       NOT NULL DEFAULT 0
);

--- Триггер при добавлении коммертария
CREATE
    OR REPLACE FUNCTION onNewComment() RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE posts
    SET comments = comments + 1
    WHERE id = new.post_id;
    RETURN new;
END;
$$;
CREATE TRIGGER newCommentTrigger
    AFTER INSERT
    ON comments
    FOR EACH ROW
EXECUTE PROCEDURE onNewComment();
---

--- Триггер при удалении комментария
CREATE
    OR REPLACE FUNCTION onRemoveComment() RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    CASE
        WHEN tg_op = 'DELETE' THEN UPDATE posts
                                   SET comments = GREATEST(comments - 1, 0)
                                   WHERE id = old.post_id;
                                   RETURN old;
        WHEN NEW.is_deleted THEN UPDATE posts
                                 SET comments = GREATEST(comments - 1, 0)
                                 WHERE id = new.post_id;
                                 RETURN new;
        ELSE RETURN new;
        END CASE;
END;
$$;
CREATE TRIGGER removeCommentTrigger
    AFTER UPDATE OR
        DELETE
    ON comments
    FOR EACH ROW
EXECUTE PROCEDURE onRemoveComment();
---

CREATE TABLE attachments
(
    id            bigserial PRIMARY KEY,
    creation_date timestamp    NOT NULL DEFAULT NOW(),
    deletion_date timestamp    NULL     DEFAULT NULL,
    is_deleted    bool         NOT NULL DEFAULT FALSE,
    name          varchar(255) NOT NULL,
    mime_type     varchar(255) NOT NULL DEFAULT 'application/octet-stream',
    hash          char(64)     NOT NULL,
    size          bigint       NOT NULL DEFAULT 0, -- bytes
    user_id       bigint       NOT NULL REFERENCES users (id)
);

CREATE TABLE message_groups
(
    id            bigserial PRIMARY KEY,
    creation_date timestamp    NOT NULL DEFAULT NOW(),
    deletion_date timestamp    NULL     DEFAULT NULL,
    is_deleted    bool         NOT NULL DEFAULT FALSE,
    name          varchar(255) NOT NULL,
    creatorId     bigint       NOT NULL REFERENCES users (id)
);

CREATE TABLE message_group_users
(
    message_group_id bigint NOT NULL REFERENCES message_groups (id),
    user_id          bigint NOT NULL REFERENCES users (id),
    CONSTRAINT pk_message_group_users PRIMARY KEY (message_group_id, user_id)
);

CREATE TABLE messages
(
    id            bigserial PRIMARY KEY,
    creation_date timestamp NOT NULL DEFAULT NOW(),
    deletion_date timestamp NULL     DEFAULT NULL,
    is_deleted    bool      NOT NULL DEFAULT FALSE,
    text          text      NOT NULL,
    user_id       BIGINT    NOT NULL REFERENCES users (id),
    user_to_id    bigint    NULL REFERENCES users (id),
    group_to_id   bigint    NULL REFERENCES message_groups (id),
    reply_to_id   bigint    NULL REFERENCES messages (id)
);

CREATE TABLE messages_attachments
(
    message_id    bigint NOT NULL REFERENCES messages (id),
    attachment_id bigint NOT NULL REFERENCES attachments (id),
    CONSTRAINT pk_messages_attachments PRIMARY KEY (message_id, attachment_id)
);

CREATE TABLE posts_likes
(
    post_id bigint NOT NULL REFERENCES posts (id),
    user_id bigint NOT NULL REFERENCES users (id),
    CONSTRAINT pk_posts_likes PRIMARY KEY (post_id, user_id)
);

--- Триггер при лайке поста
CREATE
    OR REPLACE FUNCTION onPostLike() RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE posts
    SET likes = likes + 1
    WHERE id = new.post_id;
    RETURN new;
END;
$$;
CREATE TRIGGER postLikeTrigger
    AFTER INSERT
    ON posts_likes
    FOR EACH ROW
EXECUTE PROCEDURE onPostLike();
---

--- Триггер при удалении лайка
CREATE
    OR REPLACE FUNCTION onRemovePostLike() RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE posts
    SET likes = GREATEST(likes - 1, 0)
    WHERE id = old.post_id;
    RETURN old;
END;
$$;
CREATE TRIGGER removePostLikeTrigger
    AFTER DELETE
    ON posts_likes
    FOR EACH ROW
EXECUTE PROCEDURE onRemovePostLike();
---

CREATE TABLE comments_likes
(
    comment_id bigint NOT NULL REFERENCES comments (id),
    user_id    bigint NOT NULL REFERENCES users (id),
    CONSTRAINT pk_comments_likes PRIMARY KEY (comment_id, user_id)
);

--- Триггер при лайке комментария
CREATE
    OR REPLACE FUNCTION onCommentLike() RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE comments
    SET likes = likes + 1
    WHERE id = new.comment_id;
    RETURN new;
END;
$$;
CREATE TRIGGER commentLikeTrigger
    AFTER INSERT
    ON comments_likes
    FOR EACH ROW
EXECUTE PROCEDURE onCommentLike();
---

--- Триггер при удалении лайка
CREATE
    OR REPLACE FUNCTION onRemoveCommentLike() RETURNS TRIGGER
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE posts
    SET likes = GREATEST(likes - 1, 0)
    WHERE id = old.comment_id;
    RETURN old;
END;
$$;
CREATE TRIGGER removeCommentLikeTrigger
    AFTER DELETE
    ON comments_likes
    FOR EACH ROW
EXECUTE PROCEDURE onRemoveCommentLike();
---

CREATE TABLE posts_attachments
(
    post_id       bigint NOT NULL REFERENCES posts (id),
    attachment_id bigint NOT NULL REFERENCES attachments (id),
    CONSTRAINT pk_posts_attachments PRIMARY KEY (post_id, attachment_id)
);

CREATE TABLE users_roles
(
    user_id bigint NOT NULL REFERENCES users (id),
    role_id bigint NOT NULL REFERENCES roles (id),
    CONSTRAINT pk_users_roles PRIMARY KEY (user_id, role_id)
);

CREATE TABLE users_groups
(
    user_id  bigint NOT NULL REFERENCES users (id),
    group_id bigint NOT NULL REFERENCES groups (id),
    CONSTRAINT pk_users_groups PRIMARY KEY (user_id, group_id)
);
