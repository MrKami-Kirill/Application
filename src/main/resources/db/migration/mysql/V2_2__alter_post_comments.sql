alter table post_comments add constraint fk_post_comments_parent
    foreign key (parent_id) references post_comments (id);
alter table post_comments add constraint fk_post_comments_posts
    foreign key (post_id) references posts (id);
alter table post_comments add constraint fk_post_comments_users
    foreign key (user_id) references users (id);