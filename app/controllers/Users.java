package controllers;

import models.*;
import play.Play;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.mvc.With;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;

@With(Secure.class)
public class Users extends CRUD {
    public static User getUser() {
        User user = (User) renderArgs.get("user");
        return user;
    }

    public static Long pid() {
        return new Long(1);
    }

    public static void list(int page, String search, String searchFields, String orderBy, String order) {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        if (orderBy == null) {
            orderBy = "userPosition.rate,userPosition.name,firstName";
            order = "ASC";
        }
        List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, (String) request.args.get("where"));
        Long count = type.count(search, searchFields, (String) request.args.get("where"));
        Long totalCount = type.count(null, null, (String) request.args.get("where"));
        try {
            render(type, objects, count, totalCount, page, orderBy, order, search);
        } catch (TemplateNotFoundException e) {
            render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
        }
    }

    public static void delete(String id) throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        User object = User.findById(Long.parseLong(id));
        notFoundIfNull(object);
        User owner = getUser();
        try {
            try {
                File file = new File(Play.applicationPath.getAbsolutePath() + object.profilePicture);
                if (file.exists()) file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            object._delete();
        } catch (Exception e) {
            flash.error(play.i18n.Messages.get("crud.delete.error", type.modelName));
            redirect(request.controller + ".show", object._key());
        }
        flash.success(play.i18n.Messages.get("crud.deleted", type.modelName));
        redirect(request.controller + ".list");
    }

    //        @Check(Consts.adminName)
    public static void blank() throws Exception {
        User owner = getUser();
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Model object = (Model) constructor.newInstance();
        try {
            List<UserPermission> permissions = UserPermission.find("ORDER BY queue,name").fetch();
            List<UserPosition> userPositions = UserPosition.find("ORDER BY rate,name").fetch();
            List<UserTeam> userTeams = UserTeam.find("ORDER BY name").fetch();
            render(type, object, userPositions, userTeams, permissions);
        } catch (TemplateNotFoundException e) {
            render("CRUD/blank.html", type, object);
        }
    }

    public static void create() throws Exception {
        User owner = getUser();
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        User object = (User) constructor.newInstance();
        Binder.bind(object, "object", params.all());
        validation.valid(object);
        object.username = object.username.toUpperCase();
        long count = User.find("username=?1", object.username).fetch().size();
        List<UserPermission> permissions = UserPermission.find("ORDER BY queue,name").fetch();
        if (validation.hasErrors() || count > 0 || object.profilePicture.length() == 0) {
            if (object.profilePicture.length() == 0) renderArgs.put("error", "Зураг заавал оруулах ёстой!");
            else if (count > 0) renderArgs.put("error", "Нэвтрэх нэр давхардаж байна!");
            else renderArgs.put("error", Messages.get("crud.hasErrors"));

            Boolean uniqueError = null, timeIduniqueError = null;
            if (count > 0) uniqueError = true;
            try {
                List<UserPosition> userPositions = UserPosition.find("ORDER BY rate,name").fetch();
                List<UserTeam> userTeams = UserTeam.find("ORDER BY name").fetch();
                render(request.controller.replace(".", "/") + "/blank.html", type, object, userPositions,
                        userTeams, uniqueError, timeIduniqueError, permissions);
            } catch (TemplateNotFoundException e) {
                render("CRUD/blank.html", type, object);
            }
        }
        object.password = Functions.getSha1String(object.password);
        object.permissionRelations = new ArrayList<UserPermissionRelation>();
        for (UserPermission permission : permissions) {
            int perId = Integer.parseInt(params.get("permission-" + permission.id));
            if (perId > 0) {
                UserPermissionRelation permissionRelation = new UserPermissionRelation();
                permissionRelation.user = object;
//                permissionRelation.permission = permission;
//                if (perId == 1) {
//                    permissionRelation.onlyView = true;
//                } else if (perId == 2) {
//                    permissionRelation.normal = true;
//                } else if (perId == 3) {
//                    permissionRelation.admin = true;
//                }
                object.permissionRelations.add(permissionRelation);
            }
        }
        object._save();
        flash.success(Messages.get("crud.created", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
        redirect(request.controller + ".show", object._key());
    }

    public static void show(String id) throws Exception {
        User usercheck = User.findById(Long.parseLong(id));
        User owner = getUser();
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        User object = (User) type.findById(id);
        notFoundIfNull(object);
        try {
            List<UserPermission> permissions = UserPermission.find("ORDER BY queue,name").fetch();
            List<UserPosition> userPositions = UserPosition.find("ORDER BY rate,name").fetch();
            List<UserTeam> userTeams = UserTeam.find("ORDER BY name").fetch();
            render(usercheck, type, object, userPositions, userTeams, permissions);
        } catch (TemplateNotFoundException e) {
            render("CRUD/show.html", type, object);
        }
    }

    public static void save(String id) throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        User object = User.findById(Long.parseLong(id));
        User owner = getUser();
        notFoundIfNull(object);
        String beforePassword = object.password;
        String proPic = object.profilePicture;
        Binder.bind(object, "object", params.all());
        validation.valid(object);
        List<UserPermission> permissions = UserPermission.find("ORDER BY queue,name").fetch();
        object.password = beforePassword;
        object.username = object.username.toUpperCase();
        long count = User.find("id!=?1 AND username=?2", object.id, object.username).fetch().size();
        if (validation.hasErrors() || count > 0 || object.profilePicture.length() == 0) {
            if (object.profilePicture.length() == 0) renderArgs.put("error", "Зураг заавал оруулах ёстой!");
            else if (count > 0) renderArgs.put("error", "Нэвтрэх нэр давхардаж байна!");
            else renderArgs.put("error", Messages.get("crud.hasErrors"));

            List<UserPosition> userPositions = UserPosition.find("ORDER BY rate,name").fetch();
            List<UserTeam> userTeams = UserTeam.find("ORDER BY name").fetch();
            try {
                render(request.controller.replace(".", "/") + "/show.html", type, object, userPositions, userTeams, permissions);
            } catch (TemplateNotFoundException e) {
                render("CRUD/show.html", type, object, userTeams);
            }
        }
        if (!proPic.equals(object.profilePicture) && !proPic.startsWith("/assets")) {
//            Functions.deleteFileSingle(proPic);
        }
        object.save();
        for (UserPermission permission : permissions) {
            int perId = Integer.parseInt(params.get("permission-" + permission.id));
            checkPermission(object, permission, perId);
        }

        flash.success(Messages.get("crud.saved", object));

        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        redirect(request.controller + ".show", object._key());
    }

    public static boolean checkPermission(User user, UserPermission permission, int perId) {
        boolean cont = false;
        for (UserPermissionRelation rel : user.permissionRelations) {
//            if (rel.permission.id.compareTo(permission.id) == 0) {
//                cont = true;
//                if (perId == 0) rel._delete();
//                else {
//                    rel.onlyView = false;
//                    rel.normal = false;
//                    rel.admin = false;
//                    if (perId == 1)
//                        rel.onlyView = true;
//                    else if (perId == 2)
//                        rel.normal = true;
//                    else if (perId == 3)
//                        rel.admin = true;
//                    rel._save();
//                }
//            }
        }
        if (!cont) {
            if (perId > 0) {
                UserPermissionRelation permissionRelation = new UserPermissionRelation();
                permissionRelation.user = user;
//                permissionRelation.permission = permission;
//                if (perId == 1) {
//                    permissionRelation.onlyView = true;
//                } else if (perId == 2) {
//                    permissionRelation.normal = true;
//                } else if (perId == 3) {
//                    permissionRelation.admin = true;
//                }
//                permissionRelation.create();
            }
        }
        return cont;
    }

    public static void resetPassword(Long id) {
        User user = User.findById(id);
        //  user.password = Functions.getSha1String("pass");
        //   user.save();
    }

    public static void savePassword(String oldPass, String newPass, String newRepeatPass) {
        User user = Users.getUser();
        if (!user.password.equals(Functions.getSha1String(oldPass))) {
            renderText("Хуучин нууц үг буруу байна!");
        } else if (newPass == null || newPass.length() == 0) {
            renderText("Шинэ нүүц үгээ бич нүү!");
        } else if (!newPass.equals(newRepeatPass)) {
            renderText("Дахин бичсэн нууц үг зөрж байна!");
        } else {
            user.password = Functions.getSha1String(newPass);
            user.save();
            renderText("success");
        }
    }

}
