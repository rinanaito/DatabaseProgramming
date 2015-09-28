package models;

import controllers.CRUD;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity(name = "tb_userrole")
public class UserRole extends Model {

    @Required
    public String role;

    public String description;

//    @CRUD.Hidden
//    @OneToMany(mappedBy = "userRole", cascade = CascadeType.ALL)
//    public List<User> users;

    public String toString() {
        return this.role;
    }
}
