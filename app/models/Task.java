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
@Entity(name = "tb_task")
public class Task extends Model {

    public String code;

    @Required
    public String name;

    @CRUD.Hidden
    public Date startDate;

    @CRUD.Hidden
    public Long duration;

    @Max(100)
    public Float scopePercent;

    @CRUD.Hidden
    public Date finishDate;

    public String status;

    public String depends = "";

    @CRUD.Hidden
    public Long level;

    public boolean startIsMilestone = false;

    @Max(100)
    @CRUD.Hidden
    public Float completedPercent = 0.0f;

    @CRUD.Hidden
    @Required
    @ManyToOne
    public User owner;

    @CRUD.Hidden
    public String workCount;

    @CRUD.Hidden
    public int person;

    @ManyToOne
    @CRUD.Hidden
    public ProjectObject projectObject;

    @ManyToOne
    @CRUD.Hidden
    public Task task;

    @CRUD.Hidden
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    public List<Task> tasks;

    @CRUD.Hidden
    public String floor;

    @CRUD.Hidden
    public int orderGantt;

    @CRUD.Hidden
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    public List<TaskAssignRel> taskAssignRels;

//    @CRUD.Hidden
//    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
//    public List<DailyLogScheduledWork> dailyLogScheduledWorks;
//
//    @CRUD.Hidden
//    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
//    public List<MeetingTopic> meetingTopics;

    @CRUD.Hidden
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    public List<RFI> rfis;

    @CRUD.Hidden
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    public List<PunchList> punchLists;


}
