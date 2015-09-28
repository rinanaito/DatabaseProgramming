package models;

import controllers.CRUD;
import play.db.jpa.Model;

import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * Created by enkhamgalan on 3/6/15.
 */
@MappedSuperclass
public class ModelAttach extends Model {

    @CRUD.Hidden
    public Date createdDate;

    public boolean create() {
        this.createdDate = new Date();
        return super.create();
    }

}
