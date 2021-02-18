drop table if exists post_votes;

create table post_votes (
    id integer not null auto_increment COMMENT 'id лайка/дизлайка',
    user_id integer not null COMMENT 'тот, кто поставил лайк / дизлайк',
    post_id integer not null COMMENT 'пост, которому поставлен лайк / дизлайк',
    time DATETIME not null COMMENT 'дата и время лайка / дизлайка',
    value tinyint not null COMMENT 'лайк или дизлайк: 1 или -1',
    primary key (id));