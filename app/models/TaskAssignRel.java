package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by enkhamgalan on 12/20/14.
 */
@Entity(name = "tb_task_assign_rel")
public class TaskAssignRel extends Model {

    @Required
    @ManyToOne
    public Task task;

    @Required
    @ManyToOne
    public User user;

    public int getCount(String uid) {
        return TaskAssignRel.find("user.id=?1 and task.completedPercent<?2 and" +
                " task.id=?3 order by task.startDate", Long.parseLong(uid), 100F, this.id).fetch().size();
    }
}
