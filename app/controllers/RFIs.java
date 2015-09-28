package controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import models.*;
import play.mvc.With;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Rina on 1/11/2015.
 */
@With(Secure.class)
@Check(Consts.permissionRFI)
public class RFIs extends CRUD {
    public static void viewAll() {
        list("All", "0", "", 1, "0");
    }

    public static void list(String status, String assignee, String keyword, int CurrentPageNumber, String tasky) {
        int pageLimit = 20;
        //Selector Query create
        User user = Users.getUser();
        UserTeam userTeam = Users.getUser().userTeam;
        List<RFI_Distribution> user_distributions = user.rfi_distributions;
        user_distributions.addAll(userTeam.rfi_distributions);
        String query = "project.id =" + Users.pid() + " and (";
        for (int i = 0; i < user_distributions.size(); i++) {
            if (user_distributions.get(i).rfi != null) {
                if (!user_distributions.get(i).rfi.rfi_status.status.equalsIgnoreCase("Draft"))
                    query += "id = " + user_distributions.get(i).rfi.id + " or ";
            }
        }
        query += "not(questionReceivedFrom.id <> " + Users.getUser().id + " and assignee.id <> " + Users.getUser().id + " and private_ = true))";
        int open, close, all, draft = 0, MaxPageNumber;
        String queryCount = query;
        //--------------------------------------------

        //Add assignee filter
        if (!assignee.equalsIgnoreCase("0")) query += " and assignee.id = " + assignee;
        //--------------------------------------------

        //Add task filter
        String[] tas = {""};
        if (!tasky.equalsIgnoreCase("0")) {
            tas = tasky.split("-");
            if (tas[0].equalsIgnoreCase("o")) {
                query += " and task.projectObject.id = " + tas[1];
            } else {
                query += " and task.id = " + tas[1];
            }
        } else tas[0] = tasky;
        //--------------------------------------------

        //Add status filter
        if (!status.equalsIgnoreCase("All")) {
            if (status.equalsIgnoreCase("Draft"))
                query = "rfi_status.status = 'Draft' and questionReceivedFrom.id = " + Users.getUser().id;
            else {
                query += " and rfi_status.status = '" + status + "'";
            }
        } else {
            query += " and rfi_status.status <> 'Draft'";
        }
        //--------------------------------------------

        //Add search filter
        if (!keyword.trim().equalsIgnoreCase("")) {
            query += " and ( subject like '%" + keyword + "%' or assignee.firstName like '%" + keyword + "%' or questionReceivedFrom.firstName like '%" + keyword + "%' ) ";
        } else
            keyword = "";
        //--------------------------------------------

        //Page Number counter
        List<RFI> rfiMaxSizer = RFI.find(query + " order by id desc").fetch();
        MaxPageNumber = rfiMaxSizer.size() / pageLimit;
        if (rfiMaxSizer.size() % pageLimit != 0) MaxPageNumber++;
        //Overdue calculate
        Date now = new Date();
        now = new Date(now.getTime() - (1000 * 60 * 60 * 24));
        Date week = new Date(now.getTime() + (1000 * 60 * 60 * 24 * 7));
        int weeker = 0, overdue = 0;
        for (int i = 0; i < rfiMaxSizer.size(); i++) {
            if (now.after(rfiMaxSizer.get(i).dueDate))
                overdue++;
            else if (week.after(rfiMaxSizer.get(i).dueDate))
                weeker++;
        }
        //--------------------------------------------

        //Page selector and status counter
        List<RFI> rfis = RFI.find(query + " order by createDate desc").fetch(CurrentPageNumber, pageLimit);
        open = RFI.find(queryCount + " and rfi_status.status = 'Open'").fetch().size();
        close = RFI.find(queryCount + " and rfi_status.status = 'Closed'").fetch().size();
        draft = RFI.find("project.id = " + Users.pid() + " and rfi_status.status = 'Draft' and questionReceivedFrom.id = " + Users.getUser().id).fetch().size();
        all = open + close;
        //--------------------------------------------
        List<ProjectAssignRel> rels = ProjectAssignRel.find("project.id=?1", Users.pid()).fetch();
        List<User> users = new ArrayList<User>();
        for (ProjectAssignRel rel : rels) users.add(rel.user);
        List<ProjectObject> projectObjects = ProjectObject.find("project.id = " + Users.pid()).fetch();
        render(rfis, status, tas, keyword, now, week, all, open, close, draft, users, projectObjects, overdue, weeker, CurrentPageNumber, MaxPageNumber, assignee, tasky);
    }

    public static void blank(Long assigneeId, Long taskId, String subject, String temp, String tag, Long tempId) {
        Task task = Task.findById(taskId);
        List<ProjectAssignRel> rels = ProjectAssignRel.find("project.id=?1", Users.pid()).fetch();
        List<User> users = new ArrayList<User>();
        for (ProjectAssignRel rel : rels) users.add(rel.user);
        render(users, temp, tag, subject, task, assigneeId, tempId);
    }

    public static void blankDrawing(Long assigneeId, Long taskId, String subject, String temp, String tag, Long tempId) {
        Task task = Task.findById(taskId);
        List<ProjectAssignRel> rels = ProjectAssignRel.find("project.id=?1", Users.pid()).fetch();
        List<User> users = new ArrayList<User>();
        for (ProjectAssignRel rel : rels) users.add(rel.user);
        render(users, temp, tag, subject, task, assigneeId, tempId);
    }

    public static void back(String temp, Long tempId) {
        if (temp.equalsIgnoreCase("dailylog")) ;
        if (temp.equalsIgnoreCase("report")) ;
        viewAll();
    }

    public static void create(String subject, Long assignedTo, Long task, boolean draft, Date dueDate, String location, String scheduleImpact, String costImpact, boolean private_,
                              boolean overdueNotification, String question, String[] filename, String[] filedir, String[] extension,
                              Long[] drawingId, String[] drawingPin, String[] drawingType, String distribution, String temp, Long tempId) {
        RFI rfi = new RFI();
        rfi.subject = subject;
        rfi.assignee = User.findById(assignedTo);
        rfi.createDate = new Date();
        rfi.dueDate = dueDate;
        rfi.question = question;
        rfi.project = Project.findById(Users.pid());
        if (task != null)
            rfi.task = Task.findById(task);
        rfi.location = location;
        rfi.private_ = private_;
        rfi.scheduleImpact = RFI_Impact.find("impact = ?1", scheduleImpact).first();
        rfi.costImpact = RFI_Impact.find("impact = ?1", costImpact).first();
        rfi.overdueNotification = overdueNotification;
        rfi.questionReceivedFrom = Users.getUser();
        if (!draft)
            rfi.rfi_status = RFI_Status.find("status = ?1", "Open").first();
        else
            rfi.rfi_status = RFI_Status.find("status = ?1", "Draft").first();
        rfi.create();
        if (!distribution.isEmpty()) {
            String[] distris = distribution.split(",");
            RFI_Distribution[] rfi_distributions = new RFI_Distribution[distris.length];
            for (int i = 0, j = 0; i < distris.length; i++) {
                String[] ids = distris[i].split("-");
                if (checkDist(rfi_distributions, j, ids)) {
                    rfi_distributions[j] = new RFI_Distribution(ids[0].trim(), rfi, Long.parseLong(ids[1]));
                    rfi_distributions[j].create();
                    j++;
                }
            }
        }

        if (filename != null) {
            for (int i = 0; i < filename.length; i++) {
                RFI_Attach attach = new RFI_Attach();
                attach.name = filename[i];
                attach.extension = extension[i];
                attach.dir = filedir[i];
                attach.rfi = rfi;
                attach.create();
            }
        }
        if (!draft) {
            List<User> users = new ArrayList<User>();
            users.add(rfi.assignee);
            if (rfi.distributions != null)
                for (int i = 0; i < rfi.distributions.size(); i++) {
                    RFI_Distribution dist = rfi.distributions.get(i);
                    if (dist.getCode().equalsIgnoreCase("t")) {
                        List<User> team = User.find("userTeam.id = " + dist.getId()).fetch();
                        users.addAll(team);
                    } else {
                        User dis = User.findById(dist.getId());
                        users.add(dis);
                    }
                }
            UserLiveRoom.get().notification(Users.getUser(),"RFI", users, rfi.id);
            if (temp.equalsIgnoreCase("")) {
                flash.success("Амжилттай илгээгдлээ.");
                list("Open", "0", "", 1, "0");
            }
            if (temp.equalsIgnoreCase("dailylog")) ;
            if (temp.equalsIgnoreCase("report")) ;
        } else {
            flash.success("Амжилттай хадгалагдлаа.");
            list("Draft", "0", "", 1, "0");
        }
    }

    public static void attachAjax(String filename, String filedir, String extension) {
        RFI_Attach attach = new RFI_Attach();
        attach.name = filename;
        attach.extension = extension;
        attach.dir = filedir;
        attach.create();
        renderText(attach.id);
    }

    public static void edit(Long id) {
        RFI rfi = RFI.findById(id);
        if (rfi.isRelated(Users.getUser().id)) {
            List<ProjectAssignRel> rels = ProjectAssignRel.find("project.id=?1", Users.pid()).fetch();
            List<User> users = new ArrayList<User>();
            for (ProjectAssignRel rel : rels) users.add(rel.user);
            render(rfi, users);
        } else
            viewAll();
    }

    public static void save(Long id, String subject, Long assignedTo, Long task, boolean draft, String
            drawingNumber, String dueDate, String location, String scheduleImpact, String costImpact, boolean private_,
                            boolean overdueNotification, String question, String[] filename, String[] filedir, String[] extension,
                            Long[] drawingId, String[] drawingPin, String[] drawingType, String distribution) {
        RFI rfi = RFI.findById(id);
        rfi.subject = subject;
        rfi.assignee = User.findById(assignedTo);
        rfi.project = Project.findById(Users.pid());
        if (task != null)
            rfi.task = Task.findById(task);
        if (!draft)
            rfi.rfi_status = RFI_Status.find("status = ?1", "Open").first();
        else
            rfi.rfi_status = RFI_Status.find("status = ?1", "Draft").first();
        rfi.createDate = new Date();
        rfi.dueDate = new Date(Long.parseLong(dueDate));
        rfi.location = location;
        rfi.scheduleImpact = RFI_Impact.find("impact = ?1", scheduleImpact).first();
        rfi.costImpact = RFI_Impact.find("impact = ?1", costImpact).first();
        rfi.private_ = private_;
        rfi.overdueNotification = overdueNotification;
        rfi.question = question;
        rfi.save();
        for (RFI_Distribution dist : rfi.distributions) {
            dist._delete();
        }
        rfi.distributions = new ArrayList<RFI_Distribution>();
        rfi.save();
        if (distribution.length() > 0) {
            String[] distris = distribution.split(",");
            RFI_Distribution[] rfi_distributions = new RFI_Distribution[distris.length];
            for (int i = 0, j = 0; i < distris.length; i++) {
                String[] ids = distris[i].split("-");
                if (checkDist(rfi_distributions, j, ids)) {
                    rfi_distributions[j] = new RFI_Distribution(ids[0].trim(), rfi, Long.parseLong(ids[1]));
                    rfi_distributions[j].create();
                    j++;
                }
            }
        }
        for (RFI_Attach attach : rfi.rfi_attaches) {
            attach._delete();
        }
        rfi.rfi_attaches = new ArrayList<RFI_Attach>();
        rfi.save();
        if (filename != null) {
            for (int i = 0; i < filename.length; i++) {
                RFI_Attach attach = new RFI_Attach();
                attach.name = filename[i];
                attach.extension = extension[i];
                attach.dir = filedir[i];
                attach.rfi = rfi;
                attach.create();
            }
        }
        if (!draft) {
            List<User> users = new ArrayList<User>();
            users.add(rfi.assignee);
            if (rfi.distributions != null)
                for (int i = 0; i < rfi.distributions.size(); i++) {
                    RFI_Distribution dist = rfi.distributions.get(i);
                    if (dist.getCode().equalsIgnoreCase("t")) {
                        List<User> team = User.find("userTeam.id = " + dist.getId()).fetch();
                        users.addAll(team);
                    } else {
                        User dis = User.findById(dist.getId());
                        users.add(dis);
                    }
                }
            UserLiveRoom.get().notification(Users.getUser(),"RFI", users, rfi.id);
            flash.success("Амжилттай илгээгдлээ.");
            list("Open", "0", "", 1, "0");
        } else {
            flash.success("Амжилттай хадгалагдлаа.");
            list("Draft", "0", "", 1, "0");
        }
    }

    public static void track(Long id, String note, String[] filename, String[] filedir, String[] extension) {
        RFI rfi = RFI.findById(id);
        RFI_Tracking tracking = new RFI_Tracking();
        tracking.author = Users.getUser();
        tracking.createDate = new Date();
        tracking.note = note;
        tracking.rfi = rfi;
        tracking.create();

        if (filename != null) {
            for (int i = 0; i < filename.length; i++) {
                RFI_Attach attach = new RFI_Attach();
                attach.name = filename[i];
                attach.extension = extension[i];
                attach.dir = filedir[i];
                attach.tracking = tracking;
                attach.create();
            }
        }

        flash.success("Хариу илгээгдлээ.");
        show(id);
    }

    public static void close(Long id) {
        RFI rfi = RFI.findById(id);
        if (rfi.questionReceivedFrom.id == Users.getUser().id) {
            rfi.rfi_status = RFI_Status.find("status = ?1", "Closed").first();
            rfi.closedDate = new Date();
            rfi.save();
            flash.success("Амжилттай хаагдлаа.");
            list("Closed", "0", "", 1, "0");
        } else viewAll();
    }

    public static void open(Long id) {
        RFI rfi = RFI.findById(id);
        if (rfi.questionReceivedFrom.id == Users.getUser().id) {
            rfi.rfi_status = RFI_Status.find("status = ?1", "Open").first();
            rfi.save();
            flash.success("Амжилттай нээгдлээ.");
            list("Open", "0", "", 1, "0");
        } else viewAll();

    }

    public static void show(Long id) {
        RFI rfi = RFI.findById(id);
        if (rfi.isRelated(Users.getUser().id)) {
            List<RFI_Tracking> trackings = RFI_Tracking.find("rfi.id = " + rfi.id + " order by createDate desc").fetch();
            render(rfi, trackings);
        } else
            viewAll();
    }

    public static void print(Long id) {
        RFI rfi = RFI.findById(id);
        if (rfi.isRelated(Users.getUser().id)) {
            List<RFI_Tracking> trackings = RFI_Tracking.find("rfi.id = " + id + " order by createDate desc").fetch();
            render(rfi, trackings);
        } else
            viewAll();
    }

    public static void viewDocList() {
        doclist(1);
    }

    public static void doclist(int CurrentPageNumber) {
        int pageLimit = 30;
        //Selector Query create
        User user = Users.getUser();
        UserTeam userTeam = Users.getUser().userTeam;
        List<RFI_Distribution> user_distributions = user.rfi_distributions;
        user_distributions.addAll(userTeam.rfi_distributions);
        String query = "rfi.project.id =" + Users.pid() + " and (";
        for (int i = 0; i < user_distributions.size(); i++) {
            if (user_distributions.get(i).forward != null) {
                if (!user_distributions.get(i).forward.rfi.rfi_status.status.equalsIgnoreCase("Draft"))
                    query += "id = " + user_distributions.get(i).forward.id + " or ";
            }
        }
        query += "rfi.assignee.id = " + Users.getUser().id + ")";
        int MaxPageNumber;

        //Page Number counter
        int rfiMaxSizer = RFI_Forward.find(query).fetch().size();
        MaxPageNumber = rfiMaxSizer / pageLimit;
        if (rfiMaxSizer % pageLimit != 0) MaxPageNumber++;

        //Page selector
        List<RFI_Forward> rfi_forwards = RFI_Forward.find(query + " order by createDate desc").fetch(CurrentPageNumber, pageLimit);
        //--------------------------------------------

        render(rfi_forwards, CurrentPageNumber, MaxPageNumber);
    }

    public static void docshow(Long id) {
        RFI_Forward forward = RFI_Forward.findById(id);
        if (forward.rfi.isRelated(Users.getUser().id)) {
            List<RFI_Tracking> trackings = RFI_Tracking.find("rfi.id = " + forward.rfi.id + " order by createDate desc").fetch();
            render(forward, trackings);
        } else
            viewAll();
    }

    public static void deleteF(Long id) {
        RFI_Forward forward = RFI_Forward.findById(id);
        forward._delete();
        viewDocList();
    }

    public static void forward(Long id, String description, String distribution) {
        RFI rfi = RFI.findById(id);
        RFI_Forward rfi_forward = new RFI_Forward();
        rfi_forward.createDate = new Date();
        rfi_forward.description = description;
        rfi_forward.rfi = rfi;
        rfi_forward.create();
        if (!distribution.isEmpty()) {
            String[] distris = distribution.split(",");
            RFI_Distribution[] rfi_distributions = new RFI_Distribution[distris.length];
            for (int i = 0, j = 0; i < distris.length; i++) {
                String[] ids = distris[i].split("-");
                if (checkDist(rfi_distributions, j, ids)) {
                    rfi_distributions[j] = new RFI_Distribution(ids[0].trim(), rfi_forward, Long.parseLong(ids[1]));
                    rfi_distributions[j].create();
                    j++;
                }
            }
        }
        flash.success("Дахин илгээгдлээ.");
        show(id);
    }

    public static boolean checkDist(RFI_Distribution[] rfi_distributions, int index, String[] ids) {
        for (int i = 0; i < index; i++) {
            if (ids[0].equalsIgnoreCase(rfi_distributions[i].code))
                if (ids[0].trim().equalsIgnoreCase("u")) {
                    if (rfi_distributions[i].user.id == Long.parseLong(ids[1]))
                        return false;
                } else {
                    if (rfi_distributions[i].userTeam.id == Long.parseLong(ids[1]))
                        return false;
                }
        }
        return true;
    }

}