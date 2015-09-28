package controllers;

import models.Task;
import models.TaskGroup;
import models.User;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Created by enkhamgalan on 12/21/14.
 */
public class Tasks extends CRUD {
    public static void list(Long id, int page, String search, String searchFields, String orderBy, String order) {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        Long functionArguments = id;
        String where = "taskGroup.id=" + id;
        List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, where);
        Long count = type.count(search, searchFields, where);
        Long totalCount = type.count(null, null, where);
        TaskGroup taskGroup = TaskGroup.findById(id);
        List<String[]> navigations = Functions.buildNavigation(new String[][]{
                {"/projects", Messages.get("Project")},
                {"/projectobjects?id=" + taskGroup.projectObject.project.id, taskGroup.projectObject.project.name},
                {"/taskgroups?id=" + taskGroup.projectObject.id, taskGroup.projectObject.name},
                {"/tasks?id=" + id, taskGroup.name},
        });
        try {
            render(type, objects, count, totalCount, page, orderBy, order, navigations, functionArguments);
        } catch (TemplateNotFoundException e) {
            render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order, navigations, functionArguments);
        }
    }

    public static void blank(Long gid) throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Task object = (Task) constructor.newInstance();
        TaskGroup taskGroup = TaskGroup.findById(gid);
        try {
            render(type, object, gid, taskGroup);
        } catch (TemplateNotFoundException e) {
            render("CRUD/blank.html", type, object, taskGroup);
        }
    }
//
//    public static void create(Long gid) throws Exception {
//        ObjectType type = ObjectType.get(getControllerClass());
//        notFoundIfNull(type);
//        Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
//        constructor.setAccessible(true);
//        Task object = (Task) constructor.newInstance();
//        Binder.bindBean(params.getRootParamNode(), "object", object);
//        TaskGroup taskGroup = TaskGroup.findById(gid);
//        object.taskGroup = taskGroup;
//        object.owner = Users.getUser();
//        validation.valid(object);
//        if (validation.hasErrors()) {
//            renderArgs.put("error", Messages.get("crud.hasErrors"));
//            render(request.controller.replace(".", "/") + "/blank.html", taskGroup, type, object);
//        }
//        object._save();
//        flash.success(Messages.get("crud.created", type.modelName));
//        if (params.get("_save") != null) {
//            list(gid, 1, null, null, null, null);
//        }
//        if (params.get("_saveAndAddAnother") != null) {
//            blank(gid);
//        }
//        redirect(request.controller + ".show", object._key());
//    }
//
//    public static void save(String id) throws Exception {
//        ObjectType type = ObjectType.get(getControllerClass());
//        notFoundIfNull(type);
//        Task object = Task.findById(Long.parseLong(id));
//        TaskGroup taskGroup = object.taskGroup;
//        User owner = object.owner;
//        notFoundIfNull(object);
//        Binder.bindBean(params.getRootParamNode(), "object", object);
//        object.taskGroup = taskGroup;
//        object.owner = owner;
//        validation.valid(object);
//        if (validation.hasErrors()) {
//            renderArgs.put("error", Messages.get("crud.hasErrors"));
//            try {
//                render(request.controller.replace(".", "/") + "/show.html", type, object);
//            } catch (TemplateNotFoundException e) {
//                render("CRUD/show.html", type, object);
//            }
//        }
//        object._save();
//        flash.success(Messages.get("crud.saved", type.modelName));
//        if (params.get("_save") != null) {
//            list(taskGroup.id, 1, null, null, null, null);
//        }
//        redirect(request.controller + ".show", object._key());
//    }

}
