package models;

import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity(name = "tb_car_details")
public class CarDetails extends Model {

@OneToOne(mappedBy = "details", cascade = CascadeType.ALL)
public Car car;

}
