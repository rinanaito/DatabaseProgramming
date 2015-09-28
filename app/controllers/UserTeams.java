package controllers;

import models.User;
import models.UserTeam;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.With;

import java.util.List;

@With(Secure.class)
public class UserTeams extends CRUD {
    public static void list(int page, String search, String searchFields, String orderBy, String order) {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        if (orderBy == null) {
            orderBy = "company,name";
            order = "ASC";
        }
        List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, (String) request.args.get("where"));
        Long count = type.count(search, searchFields, (String) request.args.get("where"));
        Long totalCount = type.count(null, null, (String) request.args.get("where"));
        try {
            render(type, objects, count, totalCount, page, orderBy, order);
        } catch (TemplateNotFoundException e) {
            render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
        }
    }

    public static void showUserTeamSelection(Long id) {
        List<UserTeam> userTeams = UserTeam.find("company.id=?1 order by queue,name", id).fetch();
        render(userTeams);
    }
}
