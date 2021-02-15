package main.model.entity;

import main.model.ModerationStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "posts")
@NamedQueries({
        @NamedQuery(name = "Post.findAll", query = "SELECT b FROM Post b")
        , @NamedQuery(name = "Post.findById", query = "SELECT b FROM Post b WHERE b.id = :id")
})
public class Post implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "is_active", columnDefinition = "TINYINT")
    @NotNull
    private boolean isActive;

    @Column(name = "moderation_status", columnDefinition="ENUM('NEW','ACCEPTED', 'DECLINED')")
    @Enumerated(EnumType.STRING)
    @NotNull
    private ModerationStatus moderationStatus;

    @Column(name = "moderator_id")
    private Integer moderatorId = null;

    @Column(name = "time", columnDefinition = "DATETIME")
    @NotNull
    private LocalDateTime time;

    @Column(name = "title", columnDefinition = "VARCHAR(255)")
    @NotNull
    private String title;

    @Column(name = "text", columnDefinition = "TEXT")
    @NotNull
    private String text;

    @Column(name = "view_count")
    @NotNull
    private int viewCount;

    @JoinColumn(name = "user_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_posts_users"))
    @ManyToOne(optional = false)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "idPost")
    private Set<TagToPost> tagToPosts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    private Set<PostVote> postVotes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    private Set<PostComment> postComments;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public ModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(ModerationStatus moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public Integer getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(Integer moderatorId) {
        this.moderatorId = moderatorId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<TagToPost> getTagToPosts() {
        return tagToPosts;
    }

    public void setTagToPosts(Set<TagToPost> tagToPosts) {
        this.tagToPosts = tagToPosts;
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

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", isActive=" + isActive +
                ", moderationStatus=" + moderationStatus +
                ", moderatorId=" + moderatorId +
                ", time=" + time +
                ", title=" + title +
                ", text='" + text +
                ", viewCount=" + viewCount +
                '}';
    }
}
