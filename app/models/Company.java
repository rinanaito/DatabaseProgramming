package models;

import controllers.CRUD;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: enkhamgalan
 * Date: 12/15/14
 * Time: 10:43 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity(name = "tb_company")
public class Company extends Model {

    @Required
    public String name;
//
//    @CRUD.Hidden
//    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
//    public List<User> users;
//
//    @CRUD.Hidden
//    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
//    public List<UserTeam> userTeams;

    public String toString() {
        return this.name;
    }
}
