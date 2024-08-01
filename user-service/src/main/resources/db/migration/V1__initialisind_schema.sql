create table  if  not exists token (id bigserial not null, access_token varchar(255), logged_out boolean not null, refresh_token varchar(255), user_id bigint, primary key (id));
create table  if not exists users (id bigserial not null, email varchar(255), fullname varchar(255), password varchar(255), role varchar(255) check (role in ('USER','ADMIN')), primary key (id));
alter table if exists users drop constraint if exists UK_6dotkott2kjsp8vw4d0m25fb7;

/*12345678*/
INSERT INTO users (email, fullname, password, role)
VALUES ('220107141@stu.sdu.edu.kz', 'Assylzhan Kabibulla', '$2a$12$2MC4ckkVNcvzMFyByBJiGuKlQrCQFqdgIUi3zBc9D2oqiZ4a7xEEy', 'ADMIN')
ON CONFLICT (email) DO NOTHING;
