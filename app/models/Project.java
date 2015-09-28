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
@Entity(name = "tb_project")
public class Project extends Model {

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

    public String depends = "";

    public boolean startIsMilestone = false;

    @Max(100)
    @CRUD.Hidden
    public Float completedPercent = 0.0f;

    @CRUD.Hidden
    public String workCount;

    @CRUD.Hidden
    public int person;

    @CRUD.Hidden
    public Long manHours = 0L;

    @CRUD.Hidden
    @Required
    @ManyToOne
    public User owner;

    @CRUD.Hidden
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    public List<ProjectObject> projectObjects;

    @CRUD.Hidden
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    public List<ProjectAssignRel> projectAssignRels;

    @CRUD.Hidden
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    public List<RFI> rfis;

    @CRUD.Hidden
    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL)
    public Portfolio portfolio;


    @CRUD.Hidden
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    public List<PercentHistoryProject> percentHistoryProjects;

}
