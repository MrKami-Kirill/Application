package main.rest.model.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "User.findAll", query = "SELECT b FROM User b")
        , @NamedQuery(name = "User.findById", query = "SELECT b FROM User b WHERE b.id = :id")
        , @NamedQuery(name = "User.findByName", query = "SELECT b FROM User b WHERE b.name = :name")
        , @NamedQuery(name = "User.findByEmail", query = "SELECT b FROM User b WHERE b.email = :email")
})
public class User implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_moderator", columnDefinition = "TINYINT")
    @NotNull
    private boolean isModerator;

    @Column(name = "reg_time", columnDefinition = "DATETIME")
    @NotNull
    private LocalDateTime regTime;

    @Column(name = "name", columnDefinition = "VARCHAR(255)", unique = true)
    @NotNull
    private String name;

    @Column(name = "email", columnDefinition = "VARCHAR(255)", unique = true)
    @NotNull
    private String email;

    @Column(name = "password", columnDefinition = "VARCHAR(255)")
    @NotNull
    private String password;

    @Column(name = "code", columnDefinition = "VARCHAR(255)")
    private String code;

    @Column(name = "photo", columnDefinition = "TEXT")
    private String photo;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<Post> posts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<PostVote> postVotes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<PostComment> postComments;

    public User() {
    }

    public User(@NotNull boolean isModerator, @NotNull LocalDateTime regTime, @NotNull String name, @NotNull String email, @NotNull String password) {
        this.isModerator = isModerator;
        this.regTime = regTime;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isModerator() {
        return isModerator;
    }

    public void setModerator(boolean moderator) {
        isModerator = moderator;
    }

    public LocalDateTime getRegTime() {
        return regTime;
    }

    public void setRegTime(LocalDateTime regTime) {
        this.regTime = regTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    public Set<PostVote> getPostVotes() {
        return postVotes;
    }

    public void setPostVotes(Set<PostVote> postVotes) {
        this.postVotes = postVotes;
    }

    public Set<PostComment> getPostComments() {
        return postComments;
    }

    public void setPostComments(Set<PostComment> postComments) {
        this.postComments = postComments;
    }

}
