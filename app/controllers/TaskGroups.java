package controllers;

import models.*;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Created by enkhamgalan on 12/21/14.
 */
public class TaskGroups extends CRUD {
    public static void list(Long id, int page, String search, String searchFields, String orderBy, String order) {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        if (orderBy != null) {
            if (orderBy.equals("tasks")) orderBy = "tasks.size";
        }
        Long functionArguments = id;
        String where = "projectObject.id=" + id;
        List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, where);
        Long count = type.count(search, searchFields, where);
        Long totalCount = type.count(null, null, where);
        ProjectObject projectObject = ProjectObject.findById(id);
        List<String[]> navigations = Functions.buildNavigation(new String[][]{
                {"/projects", Messages.get("Project")},
                {"/projectobjects?id=" + projectObject.project.id, projectObject.project.name},
                {"/taskGroups?id=" + id, projectObject.name},
        });
        if (orderBy != null) {
            if (orderBy.equals("tasks.size")) orderBy = "tasks";
        }
        try {
            render(type, objects, count, totalCount, page, orderBy, order, navigations, functionArguments);
        } catch (TemplateNotFoundException e) {
            render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order, navigations, functionArguments);
        }
    }

    public static void blank(Long pid) throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        TaskGroup object = (TaskGroup) constructor.newInstance();
        ProjectObject projectObject = ProjectObject.findById(pid);
        try {
            render(type, object, pid, projectObject);
        } catch (TemplateNotFoundException e) {
            render("CRUD/blank.html", type, object, projectObject);
        }
    }

    public static void create(Long pid) throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        TaskGroup object = (TaskGroup) constructor.newInstance();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        ProjectObject projectObject = ProjectObject.findById(pid);
        object.projectObject = projectObject;
        object.owner = Users.getUser();
        validation.valid(object);
        if (validation.hasErrors()) {
            renderArgs.put("error", Messages.get("crud.hasErrors"));
            render(request.controller.replace(".", "/") + "/blank.html", projectObject, type, object);
        }
        object._save();
        flash.success(Messages.get("crud.created", type.modelName));
        if (params.get("_save") != null) {
            list(pid, 1, null, null, null, null);
        }
        if (params.get("_saveAndAddAnother") != null) {
            blank(pid);
        }
        redirect(request.controller + ".show", object._key());
    }

    public static void save(String id) throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        TaskGroup object = TaskGroup.findById(Long.parseLong(id));
        ProjectObject projectObject = object.projectObject;
        User owner = object.owner;
        notFoundIfNull(object);
        Binder.bindBean(params.getRootParamNode(), "object", object);
        object.projectObject = projectObject;
        object.owner = owner;
        validation.valid(object);
        if (validation.hasErrors()) {
            renderArgs.put("error", Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/show.html", type, object);
            } catch (TemplateNotFoundException e) {
                render("CRUD/show.html", type, object);
            }
        }
        object._save();
        flash.success(Messages.get("crud.saved", type.modelName));
        if (params.get("_save") != null) {
            list(projectObject.id, 1, null, null, null, null);
        }
        redirect(request.controller + ".show", object._key());
    }

}
