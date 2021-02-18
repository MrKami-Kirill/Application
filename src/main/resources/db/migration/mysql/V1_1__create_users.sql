drop table if exists users;

create table users (
    id integer not null auto_increment COMMENT 'id пользователя',
    is_moderator TINYINT not null COMMENT 'является ли пользователь модератором',
    reg_time DATETIME not null COMMENT 'дата и время регистрации пользователя',
    name VARCHAR(255) not null COMMENT 'e-mail пользователя',
    email VARCHAR(255) not null COMMENT 'дата и время регистрации пользователя',
    password VARCHAR(255) not null COMMENT 'хэш пароля пользователя',
    code VARCHAR(255) COMMENT 'код для восстановления пароля',
    photo TEXT COMMENT 'фотография (ссылка на файл)',
    primary key (id));