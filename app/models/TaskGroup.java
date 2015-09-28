package models;

import controllers.CRUD;
import controllers.Consts;
import play.data.validation.Max;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

/**
 * Created by enkhamgalan on 12/20/14.
 */
@Entity(name = "task_group")
public class TaskGroup extends Model {

    @Required
    public String name;

    @CRUD.Hidden
    public Date startDate;

    @CRUD.Hidden
    public Long duration;

    @CRUD.Hidden
    public Date finishDate;

    @Max(100)
    @CRUD.Hidden
    public Float completedPercent = 0.0f;

    @MaxSize(Consts.maxDescriptionLength)
    public String description;

    @CRUD.Hidden
    @Required
    @ManyToOne
    public User owner;

    @Required
    @ManyToOne
    @CRUD.Hidden
    public ProjectObject projectObject;


}
