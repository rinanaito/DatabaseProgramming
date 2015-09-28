package models;

import controllers.CRUD;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by enkhamgalan on 1/11/15.
 */
@Entity
public class SampleDocAttach extends Model {

    @Required
    public String name;

    @Required
    public String dir;

    @Required
    public String extension;

    @Required
    @ManyToOne
    @CRUD.Hidden
    public SampleDoc sampleDoc;

}
