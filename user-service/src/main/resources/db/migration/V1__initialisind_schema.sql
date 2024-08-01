CREATE TABLE IF NOT EXISTS token (
                                    id BIGSERIAL NOT NULL,
                                    access_token VARCHAR(255),
                                    logged_out BOOLEAN NOT NULL,
                                    refresh_token VARCHAR(255),
                                    user_id BIGINT, PRIMARY KEY (id));

CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL NOT NULL PRIMARY KEY,
                                     email VARCHAR(255) UNIQUE,
                                     fullname VARCHAR(255),
                                     password VARCHAR(255),
                                     role VARCHAR(255) CHECK (role IN ('USER', 'ADMIN')));
ALTER TABLE users
    ADD CONSTRAINT unique_email UNIQUE (email);
ALTER TABLE IF EXISTS users DROP CONSTRAINT IF EXISTS UK_6dotkott2kjsp8vw4d0m25fb7;

/*12345678*/
INSERT INTO users (email, fullname, password, role)
VALUES ('220107141@stu.sdu.edu.kz',
        'Assylzhan Kabibulla',
        '$2a$12$2MC4ckkVNcvzMFyByBJiGuKlQrCQFqdgIUi3zBc9D2oqiZ4a7xEEy',
        'ADMIN')
ON CONFLICT (email) DO NOTHING;
