DROP TABLE IF EXISTS endpoint_hits CASCADE;

CREATE TABLE IF NOT EXISTS endpoint_hits (
    id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    app VARCHAR(255) NOT NULL,
    uri VARCHAR(2048) NOT NULL,
    ip VARCHAR(16) NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_endpoint_hits PRIMARY KEY (id)
);