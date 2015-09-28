package models;

import controllers.CRUD;
import org.hibernate.annotations.ManyToAny;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "tb_car")
public class Car extends Model {

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL)
    public CarDetails details;

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL)
    public Post post;
}
