package main.rest.model.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "post_comments")
public class PostComment implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "time", columnDefinition = "DATETIME")
    @NotNull
    private LocalDateTime time;

    @Column(name = "text", columnDefinition = "TEXT")
    @NotNull
    private String text;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentId")
    private Set<PostComment> postComments;

    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private PostComment parentId;

    @JoinColumn(name = "user_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_post_comments_users"))
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private User user;

    @JoinColumn(name = "post_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_post_comments_posts"))
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Post post;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Set<PostComment> getPostComments() {
        return postComments;
    }

    public void setPostComments(Set<PostComment> postComments) {
        this.postComments = postComments;
    }

    public PostComment getParentId() {
        return parentId;
    }

    public void setParentId(PostComment parentId) {
        this.parentId = parentId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
