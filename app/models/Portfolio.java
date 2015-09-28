package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.*;

/**
 * Created by enkhamgalan on 3/12/15.
 */
@Entity(name = "tb_portfolio")
public class Portfolio extends Model {

    @OneToOne
    public Project project;

    public String location;

    public String contractId;

    public String consumer;

    @Required
    public String performer;

    @Required
    @ManyToOne
    public ProjectCategory category;

    @Required
    public boolean isActive;

    public String imageUrl;

    public int x;
    public int y;

    @Transient
    public Integer allMeetings;
    @Transient
    public Integer overdueMeetings;
    @Transient
    public Integer nextWeekMeetings;
    @Transient
    public Integer nNextWeekMeetings;

    @Transient
    public Integer allRFIs;
    @Transient
    public Integer overdueRFIs;
    @Transient
    public Integer nextWeekRFIs;
    @Transient
    public Integer nNextWeekRFIs;

    @Transient
    public Integer allPunchLists;
    @Transient
    public Integer overduePunchLists;
    @Transient
    public Integer nextWeekPunchLists;
    @Transient
    public Integer nNextWeekPunchLists;
}
