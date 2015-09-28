package models;

import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity(name = "tb_car_mark")
public class CarMark extends Model {

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL)
    public CarDetails details;

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL)
    public Post post;
}
