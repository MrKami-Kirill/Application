package main.rest.model.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "global_settings")
@NamedQueries({
        @NamedQuery(name = "GlobalSetting.findAll", query = "SELECT b FROM GlobalSetting b")
        , @NamedQuery(name = "GlobalSetting.findById", query = "SELECT b FROM GlobalSetting b WHERE b.id = :id")
        , @NamedQuery(name = "GlobalSetting.findByCode", query = "SELECT b FROM GlobalSetting b WHERE b.code = :code")
        , @NamedQuery(name = "GlobalSetting.findByName", query = "SELECT b FROM GlobalSetting b WHERE b.name = :name")
})
public class GlobalSetting implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "code", columnDefinition = "VARCHAR(255)")
    @NotNull
    private String code;

    @Column(name = "name", columnDefinition = "VARCHAR(255)")
    @NotNull
    private String name;

    @Column(name = "value", columnDefinition = "VARCHAR(255)")
    @NotNull
    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
