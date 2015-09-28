package models;

import controllers.CRUD;
import play.data.validation.Max;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by enkhamgalan on 12/20/14.
 */
@Entity(name = "tb_project_object")
public class ProjectObject extends Model {

    public String code;

    @Required
    public String name;

    @CRUD.Hidden
    public Date startDate;

    @CRUD.Hidden
    public Long duration;

    @CRUD.Hidden
    public Date finishDate;

    public String status;

    @Max(100)
    public Float scopePercent;

    public String depends = "";

    public boolean startIsMilestone = false;

    @Max(100)
    @CRUD.Hidden
    public Float completedPercent = 0.0f;

    @CRUD.Hidden
    @Required
    @ManyToOne
    public User owner;

    @CRUD.Hidden
    public int orderGantt;

    @CRUD.Hidden
    public String workCount;

    @CRUD.Hidden
    public int person;

    @Required
    @ManyToOne
    @CRUD.Hidden
    public Project project;

    @CRUD.Hidden
    @OneToMany(mappedBy = "projectObject", cascade = CascadeType.ALL)
    public List<Task> tasks;

    @CRUD.Hidden
    @OneToMany(mappedBy = "projectObject", cascade = CascadeType.ALL)
    public List<Floor> floors;

    @CRUD.Hidden
    @OneToMany(mappedBy = "projectObject", cascade = CascadeType.ALL)
    public List<ProjectObAssignRel> projectObAssignRels;

    @CRUD.Hidden
    @OneToMany(mappedBy = "projectObject", cascade = CascadeType.ALL)
    public List<PercentHistoryObject> percentHistoryObjects;

    @Transient
    public Float previousPercent = 0.0f;

    public List<Floor> getFloors() {
        return Floor.find("projectObject.id=?1 order by name", this.id).fetch();
    }

    public List<Task> getTasks() {
        return Task.find("projectObject.id=?1 order by orderGantt", this.id).fetch();
    }
}
