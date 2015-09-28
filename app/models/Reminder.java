package models;

import controllers.CRUD;
import play.data.validation.Required;
import play.db.jpa.Model;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rina on 3/27/2015.
 */

@Entity(name = "tb_reminder")
public class Reminder extends Model {
    @Required
    @CRUD.Hidden
    @ManyToOne
    User user;

    @Required
    Date date;

    String description;

    @CRUD.Hidden
    @OneToOne
    public Meeting meeting;

    @CRUD.Hidden
    @ManyToOne
    public DailyLogMyPlan myPlan;

    public Reminder(Meeting meet, User u){
        user = u;
        meeting = meet;
        date = new Date(meeting.meetingDate.getTime() - (1000 * 60 * 30));
    }
    public Reminder(DailyLogMyPlan plan, Date d, String hour, String minute, String desc, User u){
        user = u;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.set(Calendar.HOUR,Integer.parseInt(hour));
        calendar.set(Calendar.MINUTE,Integer.parseInt(minute));
        date=calendar.getTime();
        myPlan = plan;
        description = desc;
    }
    public Date remainTime(){
        Date now = new Date();
        return new Date(date.getTime() - now.getTime());
    }
}
