package controllers;

import models.*;
import play.mvc.With;

import java.util.Date;
import java.util.List;

/**
 * Created by enkhamgalan on 2/20/15.
 */
@With(Secure.class)
public class TaskUsers {
    public static List<ProjectAssignRel> getMyProjectRels() {
        User us = Users.getUser();
        return ProjectAssignRel.find("user.id=?1 and project.completedPercent<?2 order by project.startDate", us.id, 100F).fetch();
    }

    public static List<ProjectObAssignRel> getMyProjectObRels() {
        User us = Users.getUser();
        return ProjectObAssignRel.find("user.id=?1 and projectObject.completedPercent<?2 order by projectObject.startDate", us.id, 100F).fetch();
    }

    public static List<TaskAssignRel> getMyTaskRels() {
        User us = Users.getUser();
        return TaskAssignRel.find("user.id=?1 and task.completedPercent<?2 order by task.startDate", us.id, 100F).fetch();
    }

    public static List<TaskAssignRel> getMyTaskRels(Date date) {
        User us = Users.getUser();
        return TaskAssignRel.find("user.id=?1 and task.completedPercent<?2 and" +
                " task.startDate<=?3 and task.finishDate>?4 order by task.startDate", us.id, 100F, date, date).fetch();
    }

//    public static List<DailyLogMyPlan> getMyUnScheduledTask(Date date) {
//        User us = Users.getUser();
//        return DailyLogMyPlan.find("owner.id=?1 and completedPercent<?2 and" +
//                " startDate<=?3 and finishDate>?4 order by startDate", us.id, 100F, date, date).fetch();
//    }
}
