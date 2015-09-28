package controllers;

import play.db.Model;
import play.exceptions.TemplateNotFoundException;

import java.util.List;

/**
 * Created by enkhamgalan on 12/18/14.
 */
public class Companys extends CRUD {
    public static void list(int page, String search, String searchFields, String orderBy, String order) {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        if (orderBy != null) {
            if (orderBy.equals("users")) orderBy = "users.size";
            if (orderBy.equals("userTeams")) orderBy = "userTeams.size";
        }
        List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, (String) request.args.get("where"));
        Long count = type.count(search, searchFields, (String) request.args.get("where"));
        Long totalCount = type.count(null, null, (String) request.args.get("where"));
        if (order != null) {
            if (orderBy.equals("users.size")) orderBy = "users";
            else if (orderBy.equals("userTeams.size")) orderBy = "userTeams";
        }
        try {
            render(type, objects, count, totalCount, page, orderBy, order);
        } catch (TemplateNotFoundException e) {
            render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
        }
    }
}
