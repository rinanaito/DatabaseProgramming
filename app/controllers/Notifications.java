package controllers;

import models.Notification;
import models.NotificationMessage;
import models.User;
import play.mvc.Controller;

import java.util.List;

/**
 * Created by enkhamgalan on 3/8/15.
 */
public class Notifications extends CRUD {
    public static int getNotifications(Long uid) {
        return Notification.find("acceptor.id=?1 and seen=?2", uid, false).fetch().size();
    }

    public static int getNotificationMessages(Long uid) {
        return NotificationMessage.find("acceptor.id=?1 and seen=?2", uid, false).fetch().size();
    }

    public static void notificationsHeader(Long uid, int type) {
        User user = Users.getUser();
        if (user.id.compareTo(uid) != 0) forbidden();
        if (type == 0) {
            List<Notification> notifications = Notification.find("acceptor.id=?1 order by date desc", uid).fetch(1, 20);
            render(notifications,type);
        } else {
            List<NotificationMessage> notifications = NotificationMessage.find("acceptor.id=?1 order by date desc", uid).fetch(1, 20);
            render(notifications,type);
        }
    }
}
