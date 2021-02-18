drop table if exists posts;

create table posts (
    id integer not null auto_increment COMMENT 'id поста',
    is_active TINYINT not null COMMENT 'скрыта или активна публикация: 0 или 1',
    moderation_status ENUM('NEW','ACCEPTED', 'DECLINED') not null COMMENT 'статус модерации, по умолчанию значение "NEW"',
    moderator_id integer COMMENT 'ID пользователя-модератора, принявшего решение',
    user_id integer not null COMMENT 'автор поста',
    time DATETIME not null COMMENT 'дата и время публикации поста',
    title VARCHAR(255) not null COMMENT 'заголовок поста',
    text TEXT not null COMMENT 'текст поста ',
    view_count integer not null COMMENT 'количество просмотров поста',
    primary key (id));