package main.rest.model.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tag2post")
@NamedQueries({
        @NamedQuery(name = "TagToPost.findAll", query = "SELECT b FROM TagToPost b")
        , @NamedQuery(name = "TagToPost.findById", query = "SELECT b FROM TagToPost b WHERE b.id = :id")
})
public class TagToPost implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag idTag;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post idPost;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Tag getIdTag() {
        return idTag;
    }

    public void setIdTag(Tag idTag) {
        this.idTag = idTag;
    }

    public Post getIdPost() {
        return idPost;
    }

    public void setIdPost(Post idPost) {
        this.idPost = idPost;
    }
}
