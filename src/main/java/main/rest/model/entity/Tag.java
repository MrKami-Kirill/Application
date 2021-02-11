package main.rest.model.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "tags")
@NamedQueries({
        @NamedQuery(name = "Tag.findAll", query = "SELECT b FROM Tag b")
        , @NamedQuery(name = "Tag.findById", query = "SELECT b FROM Tag b WHERE b.id = :id")
})
public class Tag implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", columnDefinition = "VARCHAR(25%)")
    private String name;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "idTag")
    private Set<TagToPost> tagToPosts;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<TagToPost> getTagToPosts() {
        return tagToPosts;
    }

    public void setTagToPosts(Set<TagToPost> tagToPosts) {
        this.tagToPosts = tagToPosts;
    }
}
