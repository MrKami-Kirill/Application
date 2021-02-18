alter table post_votes add constraint fk_post_votes_posts
    foreign key (post_id) references posts (id);
alter table post_votes add constraint fk_post_votes_users
    foreign key (user_id) references users (id);