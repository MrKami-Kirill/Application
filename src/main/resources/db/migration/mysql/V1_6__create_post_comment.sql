drop table if exists post_comments;

create table post_comments (
    id integer not null auto_increment COMMENT 'id комментария',
    parent_id integer COMMENT 'комментарий, на который оставлен этот комментарий',
    post_id integer COMMENT 'пост, к которому написан комментарий',
    user_id integer COMMENT 'автор комментария',
    time DATETIME not null COMMENT 'дата и время комментария',
    text TEXT not null COMMENT 'текст комментария',
    primary key (id));