package main.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_votes")
@NoArgsConstructor
@AllArgsConstructor
public class PostVote implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "time", columnDefinition = "DATETIME")
    @NotNull
    private LocalDateTime time;

    @Column(name = "value")
    @NotNull
    private byte value;

    @JoinColumn(name = "user_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_post_votes_users"))
    @ManyToOne(optional = false)
    private User user;

    @JoinColumn(name = "post_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_post_votes_posts"))
    @ManyToOne(optional = false)
    private Post post;

    public PostVote(@NotNull LocalDateTime time, @NotNull byte value, User user, Post post) {
        this.time = time;
        this.value = value;
        this.user = user;
        this.post = post;
    }

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

    public byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
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
