package controllers;

import controllers.MyClass.MyDay;
import controllers.MyClass.MyMonth;
import controllers.MyClass.MyWeek;
import controllers.MyClass.MyYear;
import models.*;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.With;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by enkhamgalan on 12/21/14.
 */
@With(Secure.class)
public class Projects extends CRUD {
    public static void list(int page, String search, String searchFields, String orderBy, String order) {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        if (orderBy != null && orderBy.equals("ProjectObjects")) {
            orderBy = "projectObjects.size";
        }
        List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, (String) request.args.get("where"));
        Long count = type.count(search, searchFields, (String) request.args.get("where"));
        Long totalCount = type.count(null, null, (String) request.args.get("where"));
        if (orderBy != null && orderBy.equals("projectObjects.size")) {
            orderBy = "ProjectObjects";
        }
        try {
            render(type, objects, count, totalCount, page, orderBy, order);
        } catch (TemplateNotFoundException e) {
            render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
        }
    }

    public static void create() throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Project object = (Project) constructor.newInstance();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        object.owner = Users.getUser();
        validation.valid(object);
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/blank.html", type, object);
            } catch (TemplateNotFoundException e) {
                render("CRUD/blank.html", type, object);
            }
        }
        object._save();
        flash.success(play.i18n.Messages.get("crud.created", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
        redirect(request.controller + ".show", object._key());
    }

    public static void save(String id) throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Project object = Project.findById(Long.parseLong(id));
        notFoundIfNull(object);
        User owner = object.owner;
        Binder.bindBean(params.getRootParamNode(), "object", object);
        object.owner = owner;
        validation.valid(object);
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/show.html", type, object);
            } catch (TemplateNotFoundException e) {
                render("CRUD/show.html", type, object);
            }
        }
        object._save();
        flash.success(play.i18n.Messages.get("crud.saved", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        redirect(request.controller + ".show", object._key());
    }

    public static void showActivatedProjects(int page, String search, String searchFields, String orderBy, String order) {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        String where = "completedPercent<100";
        List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, where);
        Long count = type.count(search, searchFields, where);
        Long totalCount = type.count(null, null, where);
        if (orderBy != null && orderBy.equals("projectObjects.size")) {
            orderBy = "ProjectObjects";
        }
        render(type, objects, count, totalCount, page, orderBy, order);
    }

    public static void setDates() {
        Long pid = 1L;
        List<Project> projects = Project.find("order by startDate,name").fetch();
        List<ProjectObject> projectObjects = ProjectObject.find("project.id=?1", pid).fetch();
        Project project = Project.findById(pid);
        List<MyDay> days = new ArrayList<MyDay>();
        List<MyMonth> months = new ArrayList<MyMonth>();
        List<MyWeek> weeks = new ArrayList<MyWeek>();
        List<MyYear> years = new ArrayList<MyYear>();
        int lastMonth, lastDate, lastYear;
        Calendar cal = Functions.setCalDay(Calendar.getInstance());

        String stype = "Day";
        if (stype.equals("Day")) {
            String dateFormat = "dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

            if (project.startDate != null) {
                cal.setTime(project.startDate);
                cal.setFirstDayOfWeek(Calendar.MONDAY);
                lastMonth = cal.get(Calendar.MONTH);
                months.add(new MyMonth(Functions.monthName[lastMonth], lastMonth, 1, null));
                days.add(new MyDay(cal.getTime(), simpleDateFormat.format(cal.getTime()), Functions.dayNamesMin[cal.get(Calendar.DAY_OF_WEEK) - 1], cal.get(Calendar.DAY_OF_WEEK)));
                for (int d = 1; d < project.duration; d++) {
                    cal.add(Calendar.DATE, 1);
                    days.add(new MyDay(cal.getTime(), simpleDateFormat.format(cal.getTime()), Functions.dayNamesMin[cal.get(Calendar.DAY_OF_WEEK) - 1], cal.get(Calendar.DAY_OF_WEEK)));
                    if (cal.get(Calendar.MONTH) == lastMonth) months.get(months.size() - 1).merge++;
                    else {
                        lastMonth = cal.get(Calendar.MONTH);
                        months.add(new MyMonth(Functions.monthName[lastMonth], lastMonth, 1, null));
                    }
                }
            }
        } else if (stype.equals("Week")) {
            if (project.startDate != null) {
                cal.setTime(project.startDate);
                cal.setFirstDayOfWeek(Calendar.MONDAY);
                lastMonth = cal.get(Calendar.MONTH);
                lastDate = cal.get(Calendar.WEEK_OF_MONTH);
                lastYear = cal.get(Calendar.YEAR);
                months.add(new MyMonth(Functions.monthName[lastMonth], lastMonth, 1, null));
                weeks.add(new MyWeek(lastDate + "", cal.getTime(), lastDate, cal.get(Calendar.YEAR)));
                years.add(new MyYear(lastYear + "", lastYear, 1));
                for (int d = 1; d < project.duration; d++) {
                    cal.add(Calendar.DATE, 1);
                    if (cal.get(Calendar.WEEK_OF_MONTH) != lastDate) {
                        lastDate = cal.get(Calendar.WEEK_OF_MONTH);
                        weeks.add(new MyWeek(lastDate + "", cal.getTime(), lastDate, cal.get(Calendar.YEAR)));

                        if (cal.get(Calendar.MONTH) == lastMonth) months.get(months.size() - 1).merge++;
                        else {
                            lastMonth = cal.get(Calendar.MONTH);
                            months.add(new MyMonth(Functions.monthName[lastMonth], lastMonth, 1, null));
                        }
                        if (cal.get(Calendar.YEAR) == lastYear) years.get(years.size() - 1).merge++;
                        else {
                            lastYear = cal.get(Calendar.YEAR);
                            years.add(new MyYear(lastYear + "", lastYear, 1));
                        }
                    }
                }
            }
        } else if (stype.equals("Month")) {
            if (project.startDate != null) {
                cal.setTime(project.startDate);
                cal.setFirstDayOfWeek(Calendar.MONDAY);
                lastMonth = cal.get(Calendar.MONTH);
                lastYear = cal.get(Calendar.YEAR);
                months.add(new MyMonth(Functions.monthName[lastMonth], lastMonth, cal.get(Calendar.YEAR), cal.getTime()));
                years.add(new MyYear(lastYear + "", lastYear, 1));
                for (int d = 1; d < project.duration; d++) {
                    cal.add(Calendar.DATE, 1);
                    if (cal.get(Calendar.MONTH) != lastMonth) {
                        lastMonth = cal.get(Calendar.MONTH);
                        months.add(new MyMonth(Functions.monthName[lastMonth], lastMonth, cal.get(Calendar.YEAR), cal.getTime()));
                        if (cal.get(Calendar.YEAR) == lastYear) years.get(years.size() - 1).merge++;
                        else {
                            lastYear = cal.get(Calendar.YEAR);
                            years.add(new MyYear(lastYear + "", lastYear, 1));
                        }
                    }
                }
            }
        }
        render(projects, projectObjects, project, stype, days, months, weeks, years);
    }

//
//    public static void setProjectDateVal(Long tid, Long fid, Date startDate, Date finishDate, Long duration, String stype) {
//        Project project;
//        TaskFloorRel rel = null;
//        if (fid != null && startDate != null) {
//            rel = TaskFloorRel.find("task.id=?1 and floor.id=?2", tid, fid).first();
//            if (rel != null) {
//                rel.startDate = startDate;
//                rel.finishDate = finishDate;
//                rel.duration = duration;
//                rel.save();
//            } else {
//                rel = new TaskFloorRel();
//                rel.startDate = startDate;
//                rel.finishDate = finishDate;
//                rel.duration = duration;
//                rel.task = Task.findById(tid);
//                rel.floor = Floor.findById(fid);
//                rel.create();
//            }
//            Date dateS = rel.startDate;
//            Date dateF = rel.finishDate;
//            for (TaskFloorRel rels : rel.task.taskFloorRels) {
//                if (rels.startDate != null && dateS.getTime() > rels.startDate.getTime()) {
//                    dateS = rels.startDate;
//                }
//                if (rels.finishDate != null && dateF.getTime() < rels.finishDate.getTime()) {
//                    dateF = rels.finishDate;
//                }
//            }
//            rel.task.startDate = dateS;
//            rel.task.finishDate = dateF;
//            rel.task.duration = rel.task.finishDate.getTime() - rel.task.startDate.getTime();
//            rel.task.duration = (rel.task.duration / (1000 * 60 * 60 * 24));
//            rel.task.duration++;
//            rel.task.save();
//
//            dateS = rel.startDate;
//            dateF = rel.finishDate;
//            for (Task task : rel.task.taskGroup.tasks) {
//                if (task.startDate != null && dateS.getTime() > task.startDate.getTime()) {
//                    dateS = task.startDate;
//                }
//                if (task.finishDate != null && dateF.getTime() < task.finishDate.getTime()) {
//                    dateF = task.finishDate;
//                }
//            }
//            rel.task.taskGroup.startDate = dateS;
//            rel.task.taskGroup.finishDate = dateF;
//            rel.task.taskGroup.duration = rel.task.taskGroup.finishDate.getTime() - rel.task.taskGroup.startDate.getTime();
//            rel.task.taskGroup.duration = (rel.task.taskGroup.duration / (1000 * 60 * 60 * 24));
//            rel.task.taskGroup.duration++;
//            rel.task.taskGroup.save();
//
//            dateS = rel.startDate;
//            dateF = rel.finishDate;
//            ProjectObject projectObject = rel.task.taskGroup.projectObject;
//            for (TaskGroup taskGroup : projectObject.taskGroups) {
//                if (taskGroup.startDate != null && dateS.getTime() > taskGroup.startDate.getTime()) {
//                    dateS = taskGroup.startDate;
//                }
//                if (taskGroup.finishDate != null && dateF.getTime() < taskGroup.finishDate.getTime()) {
//                    dateF = taskGroup.finishDate;
//                }
//            }
//            projectObject.startDate = dateS;
//            projectObject.finishDate = dateF;
//            projectObject.duration = projectObject.finishDate.getTime() - projectObject.startDate.getTime();
//            projectObject.duration = (projectObject.duration / (1000 * 60 * 60 * 24));
//            projectObject.duration++;
//            projectObject.save();
//
//            dateS = rel.startDate;
//            dateF = rel.finishDate;
//            project = projectObject.project;
//            for (ProjectObject object : project.projectObjects) {
//                if (object.startDate != null && dateS.getTime() > object.startDate.getTime()) {
//                    dateS = object.startDate;
//                }
//                if (object.finishDate != null && dateF.getTime() < object.finishDate.getTime()) {
//                    dateF = object.finishDate;
//                }
//            }
//            project.startDate = dateS;
//            project.finishDate = dateF;
//            project.duration = project.finishDate.getTime() - project.startDate.getTime();
//            project.duration = (project.duration / (1000 * 60 * 60 * 24));
//            project.duration++;
//            project.save();
//        } else project = Project.findById(tid);
//        List<ProjectObject> projectObjects = ProjectObject.find("project.id=?1", project.id).fetch();
//        List<MyDay> days = new ArrayList<MyDay>();
//        List<MyMonth> months = new ArrayList<MyMonth>();
//        List<MyWeek> weeks = new ArrayList<MyWeek>();
//        List<MyYear> years = new ArrayList<MyYear>();
//        int lastMonth, lastDate, lastYear;
//        Calendar cal = Functions.setCalDay(Calendar.getInstance());
//
//        if (stype.equals("Day")) {
//            String dateFormat = "dd";
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
//
//            if (project.startDate != null) {
//                cal.setTime(project.startDate);
//                cal.setFirstDayOfWeek(Calendar.MONDAY);
//                lastMonth = cal.get(Calendar.MONTH);
//                months.add(new MyMonth(Functions.monthName[lastMonth], lastMonth, 1, null));
//                days.add(new MyDay(cal.getTime(), simpleDateFormat.format(cal.getTime()), Functions.dayNamesMin[cal.get(Calendar.DAY_OF_WEEK) - 1], cal.get(Calendar.DAY_OF_WEEK)));
//                for (int d = 1; d < project.duration; d++) {
//                    cal.add(Calendar.DATE, 1);
//                    days.add(new MyDay(cal.getTime(), simpleDateFormat.format(cal.getTime()), Functions.dayNamesMin[cal.get(Calendar.DAY_OF_WEEK) - 1], cal.get(Calendar.DAY_OF_WEEK)));
//                    if (cal.get(Calendar.MONTH) == lastMonth) months.get(months.size() - 1).merge++;
//                    else {
//                        lastMonth = cal.get(Calendar.MONTH);
//                        months.add(new MyMonth(Functions.monthName[lastMonth], lastMonth, 1, null));
//                    }
//                }
//            }
//        } else if (stype.equals("Week")) {
//            if (project.startDate != null) {
//                cal.setTime(project.startDate);
//                cal.setFirstDayOfWeek(Calendar.MONDAY);
//                lastMonth = cal.get(Calendar.MONTH);
//                lastDate = cal.get(Calendar.WEEK_OF_MONTH);
//                lastYear = cal.get(Calendar.YEAR);
//                months.add(new MyMonth(Functions.monthName[lastMonth], lastMonth, 1, null));
//                weeks.add(new MyWeek(lastDate + "", cal.getTime(), lastDate, cal.get(Calendar.YEAR)));
//                years.add(new MyYear(lastYear + "", lastYear, 1));
//                for (int d = 1; d < project.duration; d++) {
//                    cal.add(Calendar.DATE, 1);
//                    if (cal.get(Calendar.WEEK_OF_MONTH) != lastDate) {
//                        lastDate = cal.get(Calendar.WEEK_OF_MONTH);
//                        weeks.add(new MyWeek(lastDate + "", cal.getTime(), lastDate, cal.get(Calendar.YEAR)));
//
//                        if (cal.get(Calendar.MONTH) == lastMonth) months.get(months.size() - 1).merge++;
//                        else {
//                            lastMonth = cal.get(Calendar.MONTH);
//                            months.add(new MyMonth(Functions.monthName[lastMonth], lastMonth, 1, null));
//                        }
//                        if (cal.get(Calendar.YEAR) == lastYear) years.get(years.size() - 1).merge++;
//                        else {
//                            lastYear = cal.get(Calendar.YEAR);
//                            years.add(new MyYear(lastYear + "", lastYear, 1));
//                        }
//                    }
//                }
//            }
//        } else if (stype.equals("Month")) {
//            if (project.startDate != null) {
//                cal.setTime(project.startDate);
//                cal.setFirstDayOfWeek(Calendar.MONDAY);
//                lastMonth = cal.get(Calendar.MONTH);
//                lastYear = cal.get(Calendar.YEAR);
//                months.add(new MyMonth(Functions.monthName[lastMonth], lastMonth, cal.get(Calendar.YEAR), cal.getTime()));
//                years.add(new MyYear(lastYear + "", lastYear, 1));
//                for (int d = 1; d < project.duration; d++) {
//                    cal.add(Calendar.DATE, 1);
//                    if (cal.get(Calendar.MONTH) != lastMonth) {
//                        lastMonth = cal.get(Calendar.MONTH);
//                        months.add(new MyMonth(Functions.monthName[lastMonth], lastMonth, cal.get(Calendar.YEAR), cal.getTime()));
//                        if (cal.get(Calendar.YEAR) == lastYear) years.get(years.size() - 1).merge++;
//                        else {
//                            lastYear = cal.get(Calendar.YEAR);
//                            years.add(new MyYear(lastYear + "", lastYear, 1));
//                        }
//                    }
//                }
//            }
//        }
//        render(rel, projectObjects, project, stype, days, months, weeks, years);
//    }
}
