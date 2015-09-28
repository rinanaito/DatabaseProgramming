package controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import controllers.MyClass.NotificationJson;
import models.*;
import play.libs.F;
import play.mvc.Controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by enkhamgalan on 3/7/15.
 */
public class UserLiveRoom extends Controller {
    // ~~~~~~~~~ Let's chat!

    final F.EventStream<UserLiveRoom.Event> chatEvents = new F.EventStream<Event>();
    public List<User> conns = new ArrayList<User>();

    /**
     * For WebSocket, when a user join the room we return a continuous event stream
     * of ChatEvent
     */
    public F.EventStream<UserLiveRoom.Event> join(User user) {
        System.out.println("JOIN: " + user.toString());
        chatEvents.publish(new Join(user));
        return chatEvents;
    }

    public void leave(User user) {
        chatEvents.publish(new Leave(user));
    }

//    public void say(User user, JsonObject json) {
//        if (json == null || json.get("message").getAsString().trim().equals("")) {
//            return;
//        }
//        chatEvents.publish(new Message(user, json));
//    }

    public void notification(User owner,String type, List<User> users, Long id) {
        List<Long> uid = new ArrayList<Long>();
        Date date = new Date();
        if (type.equals("Meeting")) {
            for (User us : users) {
                Notification notification = new Notification();
                notification.date = date;
                notification.sender = owner;
                notification.acceptor = us;
                uid.add(us.id);
                notification.create();
            }
        } else {
            RFI rfi = null;
            if (type.equals("RFI"))
                rfi = RFI.findById(id);
            for (User us : users) {
                NotificationMessage notification = new NotificationMessage();
                notification.date = date;
                notification.sender = owner;
                notification.acceptor = us;
                notification.rfi = rfi;
                uid.add(us.id);
                notification.create();
            }
        }
//        if (users.size() > 0) {
//            NotificationJson json = new NotificationJson();
//            json.type = type;
//            json.senderId = owner.id.intValue();
//            json.sender = owner.toString();
//            json.uid = uid;
//            chatEvents.publish(new NotificationEvent(json.toJsonString()));
//        }
    }
//
//    public void reminderDialog(ReminderModel reminderModel) {
//        chatEvents.publish(new NotificationReminder(reminderModel));
//    }
//    public void reminderContractDialog(ContractReminder reminderModel) {
//        chatEvents.publish(new NotificationContractReminder(reminderModel));
//    }
    public static void notificationSee(Long id) {
        Notification notification = Notification.findById(id);
        notification.seen = true;
        notification._save();
//        if (notification.meeting != null) {
//            session.put("projectId", notification.meeting.project.id);
//            session.put("projectName", notification.meeting.project.name);
//            Meetings.show(notification.meeting.id);
//        }
    }

    public static void notificationMessageSee(Long id) {
        NotificationMessage notification = NotificationMessage.findById(id);
        notification.seen = true;
        notification._save();
        if (notification.rfi != null) {
            session.put("projectId", notification.rfi.project.id);
            session.put("projectName", notification.rfi.project.name);
            RFIs.show(notification.rfi.id);
        }
    }
    public JsonObject getConUsers(String type) {
        JsonObject obj = new JsonObject();
        JsonArray jsonElements = new JsonArray();
        for (User us : this.conns) {
            JsonObject object = new JsonObject();
            object.addProperty("id", us.id);
            object.addProperty("name", us.toString());
            object.addProperty("pic", us.profilePicture);
            object.addProperty("pos", us.userPosition.name);
            jsonElements.add(object);
        }
        obj.addProperty("type", type);
        obj.add("values", jsonElements);
        return obj;
    }
    public void videoChatData(User user, String toid, String data) {
        chatEvents.publish(new VideoChat(user, toid, data));
    }
    // ~~~~~~~~~ Chat room events

    public static abstract class Event {

        final public String type;
        final public Long timestamp;

        public Event(String type) {
            this.type = type;
            this.timestamp = System.currentTimeMillis();
        }

    }
    public static class Join extends Event {

        final public User user;

        public Join(User user) {
            super("join");
            this.user = user;
        }
    }
    public static class Leave extends Event {

        final public User user;

        public Leave(User user) {
            super("leave");
            this.user = user;
        }
    }
//    public static class Message extends Event {
//
//        final public User user;
//        final public User userTo;
//        final public String text;
//        final public String date;
//
//        public Message(User uid, JsonObject json) {
//            super("message");
//            this.user = uid;
//            this.text = json.get("message").getAsString();
//            this.userTo = User.findById(json.get("toid").getAsLong());
//            MessageRel message = new MessageRel();
//            message.msgSender = this.user;
//            message.msgReceiver = this.userTo;
//            message.content = this.text;
//            message.date = new Date();
//            this.date = Consts.myDateFormat2.format(message.date);
//            message.create();
//        }
//    }
//    public static class NotificationEvent extends Event {
//        final public String obj;
//
//        public NotificationEvent(String obj) {
//            super("notification");
//            this.obj = obj;
//        }
//    }
//    public static class NotificationReminder extends Event {
//        final public ReminderModel reminderModel;
//
//        public NotificationReminder(ReminderModel model) {
//            super("remindermodelClass");
//            this.reminderModel = model;
//        }
//    }
//    public static class NotificationContractReminder extends Event {
//        final public ContractReminder contractReminder;
//
//        public NotificationContractReminder(ContractReminder model) {
//            super("contractreminderClass");
//            this.contractReminder = model;
//        }
//    }
    public static class VideoChat extends Event {
        final public String data;
        final public User user;
        final public User userTo;

        public VideoChat(User user, String toid, String data) {
            super("videochat");
            this.user = user;
            this.userTo = User.findById(Long.parseLong(toid));
//            ByteBuffer buf = null;
//            ByteArrayOutputStream b_out = new ByteArrayOutputStream();
//            try {
//                ObjectOutputStream o_out = new ObjectOutputStream(b_out);
//                o_out.writeObject(json.get("vdata").getAsString());
//                o_out.close();
//            } catch (IOException e) {
//            }
//            byte[] imageData = b_out.toByteArray();
//            try {
//                buf = ByteBuffer.wrap(imageData);
//            } catch (Throwable ioe) {
//                System.out.println("Error sending message " + ioe.getMessage());
//            }
//            JsonObject object = new JsonObject();
            this.data = data;
        }
    }

    // ~~~~~~~~~ Chat room factory

    static UserLiveRoom instance = null;

    public static UserLiveRoom get() {
        if (instance == null) {
            instance = new UserLiveRoom();
        }
        return instance;
    }

}

