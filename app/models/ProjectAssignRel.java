package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by enkhamgalan on 2/20/15..
 */
@Entity(name = "tb_project_assign_rel")
public class ProjectAssignRel extends Model {

    @Required
    @ManyToOne
    public Project project;

    @Required
    @ManyToOne
    public User user;

    public int getCount(String uid) {
        return ProjectObAssignRel.find("user.id=?1 and projectObject.completedPercent<?2" +
                " and projectObject.project.id=?3 order by projectObject.startDate", Long.parseLong(uid), 100F, this.project.id).fetch().size();
    }
}
