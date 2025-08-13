-- CREATE DATABASE gitlab_analytics;

CREATE TABLE IF NOT EXISTS projects
(
    id                  bigint
        PRIMARY KEY,
    name                text    NOT NULL,
    archived            boolean NOT NULL,
    path_with_namespace text    NOT NULL,
    web_url             text    NOT NULL,
    tags                varchar(255)[],
    last_updated        timestamp WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS merge_requests
(
    id              bigserial
        PRIMARY KEY,
    mr_id           bigint                   NOT NULL,
    project_id      bigint                   NOT NULL
        REFERENCES projects (id),
    title           text                     NOT NULL,
    author_username text                     NOT NULL,
    author_name     text,
    web_url         text                     NOT NULL,
    created_at      timestamp WITH TIME ZONE NOT NULL,
    updated_at      timestamp WITH TIME ZONE NOT NULL,
    merged_at       timestamp WITH TIME ZONE,
    closed_at       timestamp WITH TIME ZONE,
    comments_count  integer                           DEFAULT 0,
    upvotes         integer                           DEFAULT 0,
    downvotes       integer                           DEFAULT 0,
    state           text                     NOT NULL,
    target_branch   text                     NOT NULL,
    source_branch   text                     NOT NULL,
    last_sync_at    timestamp WITH TIME ZONE NOT NULL,
    announced       boolean                  NOT NULL DEFAULT FALSE,
    CONSTRAINT unique_mr
        UNIQUE (mr_id, project_id)
);

CREATE TABLE IF NOT EXISTS projects
(
    id                  bigint
        PRIMARY KEY,
    username_gl         varchar(255) NOT NULL,
    username_tg         varchar(255),
    path_with_namespace text         NOT NULL,
    web_url             text         NOT NULL,
    tags                varchar(255)[],
    last_updated        timestamp WITH TIME ZONE
);

DROP TABLE users;
CREATE TABLE IF NOT EXISTS users
(
    id          serial
        PRIMARY KEY,
    username_gl varchar(255) NOT NULL,
    username_tg varchar(255),
    tags        varchar(255)[]
);

-- DROP TABLE projects CASCADE;
DROP TABLE merge_requests CASCADE;

CREATE USER gitlab_analytics_user WITH PASSWORD 'qwerty123';

GRANT ALL PRIVILEGES ON DATABASE gitlab_analytics TO gitlab_analytics_user;
GRANT ALL PRIVILEGES ON TABLE projects TO gitlab_analytics_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO gitlab_analytics_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO gitlab_analytics_user;
GRANT ALL PRIVILEGES on csp_core_backend_merge_request_v to gitlab_analytics_user;
GRANT ALL PRIVILEGES on common_merge_requests_v to gitlab_analytics_user;