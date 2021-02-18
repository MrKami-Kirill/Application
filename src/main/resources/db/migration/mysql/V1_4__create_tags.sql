drop table if exists tags;

create table tags (
    id integer not null auto_increment COMMENT 'id тэга',
    name VARCHAR(255) COMMENT 'текст тэга',
    primary key (id));