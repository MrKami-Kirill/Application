alter table tag2post add constraint fk_tag2post_post
    foreign key (post_id) references posts (id);
alter table tag2post add constraint fk_tag2post_tag
    foreign key (tag_id) references tags (id);