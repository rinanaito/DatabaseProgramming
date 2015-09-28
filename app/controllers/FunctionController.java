package controllers;

import models.*;
import play.Play;
import play.mvc.Controller;
import play.mvc.Http;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class FunctionController extends Controller {

    private static String controllerName = "FunctionController";

    public static List<User> getUsers() {
        return User.find("active=true ORDER BY userTeam.name, userPosition.rate, firstName").fetch();
    }

    public static void getSelectorInfo(Long id, int type) {
        if (type == 1) {
            User obj = User.findById(id);
            if (obj != null)
                render(controllerName + "/getSelectorInfo.html", obj, type);
        }
    }

    public static void getUserInfo(String username, Long uid) {
        User us;
        if (uid != null) us = User.findById(uid);
        else {
            username = username.replace(" ", "");
            us = User.find("username=?1", username).first();
        }
        render(controllerName + "/userInfo.html", us);
    }

    public static void calendarList(String dateVal, int action) {
        String cals[] = Functions.CalendarList(dateVal, action);
        String day = cals[0];
        String month = cals[1];
        String year = cals[2];
        String dom = cals[3];
        String mprev = cals[4];
        String mnow = cals[5];
        String mnext = cals[6];
        render(day, month, year, dom, mprev, mnow, mnext);
    }

    public static void downloadFile(String fileDir, String fileName, String extension) throws IOException, GeneralSecurityException {
        String downloadUrl = fileDir + "." + extension;
        java.io.File file = new java.io.File(Play.applicationPath.getAbsoluteFile() + downloadUrl);
        Http.Response.current().contentType = "application/octet-stream";
        try {
            String des = fileName + "." + extension;
            renderBinary(new FileInputStream(file), des, "application/octet-stream", false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            notFound();
        }
    }

    public static void getMyTaskRels(int type, Long uid, Long tid) {
        if (type == 0) {
            List<ProjectObAssignRel> assignRels = ProjectObAssignRel.find("user.id=?1 and projectObject.project.id=?2 and projectObject.completedPercent<?3 order by projectObject.startDate", uid, tid, 100F).fetch();
            render(assignRels, type, tid, uid);
        } else {
            List<TaskAssignRel> assignRels = TaskAssignRel.find("user.id=?1 and task.projectObject.id=?2 and task.completedPercent<?3 order by task.startDate", uid, tid, 100F).fetch();
            render(assignRels, type, tid, uid);
        }
    }

    public static void getTaskUserObjects(String stype, int otype, Long oid) {
        if (oid == null) oid = 0L;
        if (stype.equals("Task")) {
            if (otype == 0) {
                List<Project> objects = Project.findAll();
                render(objects, stype, otype, oid);
            } else if (otype == 1) {
                List<ProjectObject> objects = ProjectObject.find("project.id=?1", oid).fetch();
                render(objects, stype, otype, oid);
            } else if (otype == 2) {
                List<Task> objects = Task.find("projectObject.id=?1", oid).fetch();
                render(objects, stype, otype, oid);
            } else if (otype == 3) {
                List<Task> objects = Task.find("task.id=?1", oid).fetch();
                render(objects, stype, otype, oid);
            }
        } else {
            if (otype == 0) {
                List<UserTeam> objects = UserTeam.find("order by queue, name").fetch();
                render(objects, stype, otype, oid);
            } else if (otype == 1) {
                List<User> objects = User.find("userTeam.id=?1 order by userPosition.rate, firstName", oid).fetch();
                render(objects, stype, otype, oid);
            }
        }
    }
}
