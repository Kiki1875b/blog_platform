CREATE TABLE members (
                         id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         email        VARCHAR(100) NOT NULL UNIQUE,
                         nickname	  VARCHAR(50),
                         name         VARCHAR(50),
                         password     VARCHAR(100),
                         provider     VARCHAR(20),
                         provider_id  VARCHAR(255),
                         status       VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                             CHECK (status IN ('ACTIVE', 'DELETED')),
                         profile_url  VARCHAR(255),
                         role 		  VARCHAR(50) not null default 'USER' check (role in ('USER', 'ADMIN')),
                         created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                         updated_at   TIMESTAMPTZ,
                         CONSTRAINT uk_members_provider UNIQUE (provider, provider_id)
);


CREATE TABLE refresh_token (
                               member_id UUID PRIMARY KEY,                  -- FK to members.id
                               token     VARCHAR(512) NOT NULL,             -- 실제 refresh token 값
                               created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                               updated_at TIMESTAMPTZ
);

CREATE TABLE blogs (
                       id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       member_id     UUID NOT NULL,
                       title       VARCHAR(100) NOT NULL,
                       description VARCHAR(255) NOT NULL,
                       visibility  VARCHAR(20) NOT NULL DEFAULT 'PUBLIC'
                           CHECK (visibility IN ('PUBLIC', 'PRIVATE')),
                       slug        VARCHAR(50) NOT NULL UNIQUE,
                       created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       updated_at  TIMESTAMPTZ,
                       CONSTRAINT fk_blogs_owner FOREIGN KEY (member_id)
                           REFERENCES members(id) ON DELETE CASCADE
);

CREATE TABLE member_blog_follow (
                                    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    blog_id    UUID NOT NULL,
                                    member_id    UUID NOT NULL,
                                    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                    updated_at TIMESTAMPTZ null,
                                    CONSTRAINT uk_member_blog_follow UNIQUE (blog_id, member_id),
                                    CONSTRAINT fk_follow_blog   FOREIGN KEY (blog_id) REFERENCES blogs(id)   ON DELETE CASCADE,
                                    CONSTRAINT fk_follow_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);



CREATE TABLE blog_stat (
                           id              UUID PRIMARY KEY,
                           post_count      BIGINT NOT NULL DEFAULT 0,
                           view_count      BIGINT NOT NULL DEFAULT 0,
                           follower_count  BIGINT NOT NULL DEFAULT 0,
                           created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                           updated_at TIMESTAMPTZ null,
                           CONSTRAINT fk_blog_stat_blog FOREIGN KEY (id)
                               REFERENCES blogs(id) ON DELETE CASCADE
);

CREATE TABLE tags (
                      id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                      name       VARCHAR(50) NOT NULL UNIQUE,
                      created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


CREATE TABLE blog_tags (
                           id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           blog_id    UUID NOT NULL,
                           tag_id     UUID NOT NULL,
                           created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                           CONSTRAINT uk_blog_tags unique (blog_id, tag_id),
                           CONSTRAINT fk_blog_tags_blog FOREIGN KEY (blog_id) REFERENCES blogs(id) ON DELETE CASCADE,
                           CONSTRAINT fk_blog_tags_tag  FOREIGN KEY (tag_id)  REFERENCES tags(id)  ON DELETE CASCADE
);

CREATE TABLE posts (
                       id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       blog_id      UUID NOT NULL,             -- 게시된 블로그
                       member_id      UUID NOT NULL,             -- 작성자(멤버)
                       title        VARCHAR(200) NOT NULL,
                       content      TEXT NOT NULL,
                       state        VARCHAR(16) NOT NULL DEFAULT 'PUBLIC'
                           CHECK (state IN ('PUBLIC','PRIVATE')),
                       created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       updated_at   TIMESTAMPTZ,

                       CONSTRAINT fk_posts_blog  FOREIGN KEY (blog_id) REFERENCES blogs(id)   ON DELETE CASCADE,
                       CONSTRAINT fk_posts_user  FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);

CREATE TABLE post_tags (
                           id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           post_id    UUID NOT NULL,
                           tag_id     UUID NOT NULL,
                           created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                           CONSTRAINT uk_post_tags UNIQUE (post_id, tag_id),
                           CONSTRAINT fk_post_tags_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                           CONSTRAINT fk_post_tags_tag  FOREIGN KEY (tag_id)  REFERENCES tags(id)  ON DELETE CASCADE
);

CREATE TABLE comments (
                          id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          post_id     UUID NOT NULL,
                          member_id     UUID NOT NULL,
                          content     VARCHAR(3000) NOT NULL,
                          is_deleted  BOOLEAN NOT NULL DEFAULT FALSE,
                          parent_id   UUID,                       -- NULL = 루트 댓글
                          created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          updated_at  TIMESTAMPTZ,

                          CONSTRAINT fk_comments_post   FOREIGN KEY (post_id)  REFERENCES posts(id)   ON DELETE CASCADE,
                          CONSTRAINT fk_comments_member FOREIGN KEY (member_id)  REFERENCES members(id) ON DELETE CASCADE,
                          CONSTRAINT fk_comments_parent FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE CASCADE
);

CREATE TABLE post_likes (
                            id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            post_id    UUID NOT NULL,
                            member_id    UUID NOT NULL,
                            created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                            CONSTRAINT uk_post_likes UNIQUE (post_id, member_id),
                            CONSTRAINT fk_post_likes_post   FOREIGN KEY (post_id) REFERENCES posts(id)   ON DELETE CASCADE,
                            CONSTRAINT fk_post_likes_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);


CREATE TABLE comment_likes (
                               id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               comment_id UUID NOT NULL,
                               member_id    UUID NOT NULL,
                               created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                               CONSTRAINT uk_comment_likes unique (comment_id, member_id),
                               CONSTRAINT fk_comment_likes_comment FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE,
                               CONSTRAINT fk_comment_likes_member  FOREIGN KEY (member_id)    REFERENCES members(id) ON DELETE CASCADE
);

CREATE TABLE post_attachments (
                                  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  post_id     UUID NOT NULL,
                                  member_id     UUID NOT NULL,                 -- 업로더
                                  file_url    VARCHAR(2048) NOT NULL,
                                  file_type   VARCHAR(50),
                                  file_size   BIGINT,
                                  created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                                  CONSTRAINT fk_post_attachments_post   FOREIGN KEY (post_id) REFERENCES posts(id)   ON DELETE CASCADE,
                                  CONSTRAINT fk_post_attachments_member FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);
