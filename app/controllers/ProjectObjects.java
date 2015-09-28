package controllers;

import models.Project;
import models.ProjectObject;
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
public class ProjectObjects extends CRUD {
    public static void list(Long id, int page, String search, String searchFields, String orderBy, String order) {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        if (orderBy != null) {
            if (orderBy.equals("taskGroups")) orderBy = "taskGroups.size";
            else if (orderBy.equals("floors")) orderBy = "floors.size";
        }
        Long functionArguments = id;
        String where = "project.id=" + id;
        List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, where);
        Long count = type.count(search, searchFields, where);
        Long totalCount = type.count(null, null, where);
        Project project = Project.findById(id);
        List<String[]> navigations = Functions.buildNavigation(new String[][]{
                {"/projects", Messages.get("Project")},
                {"/projectobjects?id=" + id, project.name},
        });
        if (orderBy != null) {
            if (orderBy.equals("taskGroups.size")) orderBy = "taskGroups";
            else if (orderBy.equals("floors.size")) orderBy = "floors";
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
        ProjectObject object = (ProjectObject) constructor.newInstance();
        Project project = Project.findById(pid);
        try {
            render(type, object, pid, project);
        } catch (TemplateNotFoundException e) {
            render("CRUD/blank.html", type, object, project);
        }
    }

    public static void create(Long pid) throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        ProjectObject object = (ProjectObject) constructor.newInstance();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        Project project = Project.findById(pid);
        object.project = project;
        object.owner = Users.getUser();
        validation.valid(object);
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            render(request.controller.replace(".", "/") + "/blank.html", project, type, object);
        }
        object._save();
        flash.success(play.i18n.Messages.get("crud.created", type.modelName));
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
        ProjectObject object = ProjectObject.findById(Long.parseLong(id));
        Project project = object.project;
        User owner = object.owner;
        notFoundIfNull(object);
        Binder.bindBean(params.getRootParamNode(), "object", object);
        object.project = project;
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
            list(project.id, 1, null, null, null, null);
        }
        redirect(request.controller + ".show", object._key());
    }

}
