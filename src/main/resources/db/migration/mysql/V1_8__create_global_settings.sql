drop table if exists global_settings;

create table global_settings (
    id integer not null auto_increment COMMENT 'id настройки',
    code VARCHAR(255) not null COMMENT 'системное имя настройки',
    name VARCHAR(255) not null COMMENT 'название настройки',
    value VARCHAR(255) not null COMMENT 'значение настройки',
    primary key (id));