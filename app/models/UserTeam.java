package models;

import controllers.CRUD;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.List;

@Entity(name = "tb_userteam")
public class UserTeam extends Model {

    @Required
    public String name;

    @Required
    public Long queue = 1L;

    public boolean contractor = false;

    @CRUD.Hidden
    @OneToMany(mappedBy = "userTeam")
    public List<User> users;

    @CRUD.Hidden
    @OneToMany(mappedBy = "userTeam", cascade = CascadeType.ALL)
    public List<RFI_Distribution> rfi_distributions;

    @CRUD.Hidden
    @OneToMany(mappedBy = "userTeam", cascade = CascadeType.ALL)
    public List<PunchList_Distribution> punchList_distributions;

    public String toString() {
        return this.name;
    }

    public List<User> getAllUsers() {
        return User.find("userTeam.id=? and userRole.role.id!=1 and userPosition.name!='System' order by userPosition.queue.num,userPosition.name,firstname", this.id).fetch();
    }

    public List<User> getSelectedUsers() {
        return User.find("userTeam.id=? and userRole.id=? order by userPosition.rate,userPosition.name,firstname", this.id, 1L).fetch();
    }

    @Transient
    public int userEng = 0;

}
