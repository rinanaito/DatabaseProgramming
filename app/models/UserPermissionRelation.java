package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by enkhamgalan on 3/21/15.
 */
@Entity(name = "tb_userpermissionrelation")
public class UserPermissionRelation extends Model {

    @ManyToOne
    public User user;

    @ManyToOne
    public UserPermissionType permissionType;

}
