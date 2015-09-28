package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by enkhamgalan on 2/20/15..
 */
@Entity(name = "tb_projectob_assign_rel")
public class ProjectObAssignRel extends Model {

    @Required
    @ManyToOne
    public ProjectObject projectObject;

    @Required
    @ManyToOne
    public User user;

    public int getCount(String uid) {
        return TaskAssignRel.find("user.id=?1 and task.completedPercent<?2 and" +
                " task.projectObject.id=?3 order by task.startDate", Long.parseLong(uid), 100F, this.projectObject.id).fetch().size();
    }
}
