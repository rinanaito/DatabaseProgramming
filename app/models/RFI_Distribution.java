package models;

import controllers.CRUD;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rina on 1/11/2015.
 */
@Entity(name = "tb_rfi_distribution")
public class RFI_Distribution extends Model {
    public String code;

    @ManyToOne
    @CRUD.Hidden
    public User user;

    @ManyToOne
    @CRUD.Hidden
    public UserTeam userTeam;

    @ManyToOne
    @CRUD.Hidden
    public RFI rfi;

    @ManyToOne
    @CRUD.Hidden
    public RFI_Forward forward;

    public RFI_Distribution(String code_, RFI rfi_, Long id) {
        code = code_;
        rfi = rfi_;
        if (code.equalsIgnoreCase("u")) {
            user = User.findById(id);
        } else {
            userTeam = UserTeam.findById(id);
        }
    }

    public RFI_Distribution(String code_, RFI_Forward forward_, Long id) {
        code = code_;
        forward = forward_;
        if (code.equalsIgnoreCase("u")) {
            user = User.findById(id);
        } else {
            userTeam = UserTeam.findById(id);
        }
    }

    public String getName() {
        if (code.equalsIgnoreCase("u"))
            return user.getLastnameFirstCharacter() + "." + user.firstName;
        else
            return userTeam.name;
    }

    public Long getId() {
        if (code.equalsIgnoreCase("u"))
            return user.id;
        else
            return userTeam.id;

    }


    public String getCode() {
        return code;
    }
}
