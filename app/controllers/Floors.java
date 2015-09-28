package controllers;

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
public class Floors extends CRUD {
    public static void list(Long id, int page, String search, String searchFields, String orderBy, String order) {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
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
                {"/floors?id=" + id, projectObject.name},
        });
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
    }

    public static void create(Long pid) throws Exception {
    }

    public static void save(String id) throws Exception {
    }

}
