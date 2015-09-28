package models;

import play.db.jpa.Model;

import javax.persistence.*;

@Entity(name = "tb_comment")
public class Comment extends Model {

    @ManyToOne
    public Post post;
}
