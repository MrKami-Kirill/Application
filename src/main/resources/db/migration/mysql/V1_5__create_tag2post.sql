drop table if exists tag2post;

create table tag2post (
    id integer not null auto_increment COMMENT 'id связи',
    post_id integer not null COMMENT 'id поста',
    tag_id integer not null COMMENT 'id тэга',
    primary key (id));