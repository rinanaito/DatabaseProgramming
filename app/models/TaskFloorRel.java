package models;

import controllers.CRUD;
import play.data.validation.Max;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created by enkhamgalan on 12/20/14.
 */
@Entity(name = "task_floor_rel")
public class TaskFloorRel extends Model {

    @Max(100)
    @CRUD.Hidden
    public Float completedPercent = 0.0f;

    @CRUD.Hidden
    public Date startDate;

    @CRUD.Hidden
    public Long duration;

    @CRUD.Hidden
    public Date finishDate;

    @Required
    @ManyToOne
    public Task task;

    @Required
    @ManyToOne
    public Floor floor;
}
