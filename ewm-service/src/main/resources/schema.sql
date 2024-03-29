DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS comments_event CASCADE;
DROP TABLE IF EXISTS claims CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS compilation_event CASCADE;

CREATE TABLE IF NOT EXISTS users (
	id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	name VARCHAR(250) NOT NULL,
	email VARCHAR(254) NOT NULL,
	CONSTRAINT pk_user PRIMARY KEY (id),
	CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories (
	id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY,
	name VARCHAR(50) NOT NULL,
	CONSTRAINT pk_category PRIMARY KEY (id),
	CONSTRAINT uq_category_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    category_id BIGINT NOT NULL,
    initiator_id BIGINT NOT NULL,
    lat REAL NOT NULL,
    lon REAL NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    is_paid BOOL NOT NULL DEFAULT FALSE,
    is_moderation BOOL NOT NULL DEFAULT TRUE,
    participant_limit BIGINT NOT NULL DEFAULT 0,
    title VARCHAR(120) NOT NULL,
    annotation VARCHAR(2000) NOT NULL,
    description VARCHAR(7000) NOT NULL,
    state_id INTEGER NOT NULL,
    confirmed_requests BIGINT,
    views BIGINT,
    CONSTRAINT pk_event PRIMARY KEY (id),
    CONSTRAINT fk_event_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    CONSTRAINT fk_event_initiator FOREIGN KEY (initiator_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments_event (
    id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    event_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    edited_on TIMESTAMP WITHOUT TIME ZONE,
    text VARCHAR(2048) NOT NULL,
    state_id INTEGER NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id),
    CONSTRAINT fk_comment_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS claims (
    id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    author_id BIGINT NOT NULL,
    comment_id BIGINT NOT NULL,
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    cause_id INTEGER NOT NULL,
    CONSTRAINT pk_claim PRIMARY KEY (id),
    CONSTRAINT fk_claim_comment FOREIGN KEY (comment_id) REFERENCES comments_event(id) ON DELETE CASCADE,
    CONSTRAINT fk_claim_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    event_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    status INTEGER NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT fk_event_request FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_requester_request FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS compilations (
    id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    title VARCHAR(50) NOT NULL,
    pinned BOOL DEFAULT FALSE,
    CONSTRAINT pk_comp PRIMARY KEY (id),
    CONSTRAINT uq_compilation_title UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS compilation_event (
    comp_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    CONSTRAINT pk_comp_event PRIMARY KEY (comp_id, event_id),
    CONSTRAINT fk_comp_event_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_comp_event_comp FOREIGN KEY (comp_id) REFERENCES compilations(id) ON DELETE CASCADE
);