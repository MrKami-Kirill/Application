alter table posts add constraint fk_posts_users
    foreign key (user_id) references users (id);