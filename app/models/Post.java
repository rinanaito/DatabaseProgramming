package models;

import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

@Entity(name = "tb_post")
public class Post extends Model {

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL)
    public Car car;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    public List<Comment> comments;
}
