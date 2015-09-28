package models;

import controllers.CRUD;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Created by Rina on 1/11/2015.
 */
@Entity
public class Person extends Model {

    @Required
    public String firstname;
    public String lastname;
    public Long age;

    public String toString() {
        return this.firstname + this.lastname;
    }
}
