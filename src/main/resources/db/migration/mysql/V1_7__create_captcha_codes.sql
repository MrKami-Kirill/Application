drop table if exists captcha_codes;

create table captcha_codes (
    id integer not null auto_increment COMMENT 'id каптча',
    time DATETIME not null COMMENT 'дата и время генерации кода капчи',
    code TINYTEXT not null COMMENT 'код, отображаемый на картинкке капчи',
    secret_code TINYTEXT not null COMMENT 'код, передаваемый в параметре',
    primary key (id));