package models;

import controllers.CRUD;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.List;

@Entity(name = "tb_user")
public class User extends Model {

    @Required
    public String firstName;

    @Required
    public String lastName;

    @Required
    public String surName;

    @Required
    public String username;

    @Required
    public String password;

    @Required
    public String email;

    @Required
    public String phone1;

    public String phone2;

    public String address1;
    public String address2;
    public String address3;
    public String address4;

    public int x;
    public int y;
    public int w;
    public int h;
    public boolean active = true;

    @ManyToOne
    public UserTeam userTeam;

    @Required
    @ManyToOne
    public UserPosition userPosition;

    @CRUD.Hidden
    @Required
    public String profilePicture;

    @CRUD.Hidden
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    public AccountSetting accountSetting;

//    @CRUD.Hidden
//    @OneToMany(mappedBy = "uploader", cascade = CascadeType.ALL)
//    public List<FileShare> fileShares;
//
//    @CRUD.Hidden
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    public List<FileShareReceivedUser> receivedUsers;

    @CRUD.Hidden
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<TaskAssignRel> taskAssignRels;

    @CRUD.Hidden
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<ProjectAssignRel> projectAssignRels;
    @CRUD.Hidden
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<ProjectObAssignRel> projectObAssignRels;

    @OneToMany(mappedBy = "assignee", cascade = CascadeType.ALL)
    public List<RFI> rfis;

    @OneToMany(mappedBy = "questionReceivedFrom", cascade = CascadeType.ALL)
    public List<RFI> rfiList;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    public List<RFI_Tracking> trackings;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<RFI_Distribution> rfi_distributions;


//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    public List<MeetingUserRel> meetingUserRels;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    public List<MeetingTopicUserRel> meetingTopicUserRels;

//    @CRUD.Hidden
//    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
//    public List<MeetingTopic> meetingTopics;
//
//    @CRUD.Hidden
//    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
//    public List<DailyLogWorkNote> dailyLogWorkNotes;

    @CRUD.Hidden
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    public List<Notification> notifications1;

    @CRUD.Hidden
    @OneToMany(mappedBy = "acceptor", cascade = CascadeType.ALL)
    public List<Notification> notifications2;

    @CRUD.Hidden
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    public List<NotificationMessage> notificationMessages1;

    @CRUD.Hidden
    @OneToMany(mappedBy = "acceptor", cascade = CascadeType.ALL)
    public List<NotificationMessage> notificationMessages2;

    @CRUD.Hidden
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<UserPermissionRelation> permissionRelations;

//    @OneToMany(mappedBy = "msgSender", cascade = CascadeType.ALL)
//    public List<MessageRel> messages1;
//
//    @OneToMany(mappedBy = "msgReceiver", cascade = CascadeType.ALL)
//    public List<MessageRel> messages2;
//
//    @CRUD.Hidden
//    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
//    public List<DailyLogWeather> weathers;
//
//    @CRUD.Hidden
//    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
//    public List<DailyLogTechnicalDelay> technicalDelays;
//
//    @CRUD.Hidden
//    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
//    public List<DailyLogDelivery> deliveries;
//
//    @CRUD.Hidden
//    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
//    public List<DailyLogInspection> inspections;
//
//    @CRUD.Hidden
//    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
//    public List<DailyLogNote> notesLog;
//
//    @CRUD.Hidden
//    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
//    public List<DailyLogSanaachlaga> sanaachlagas;
//
//    @CRUD.Hidden
//    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
//    public List<DailyLogVisitor> visitors;


    public String toString() {
        return this.firstName + "." + this.getLastnameFirstCharacter();
    }

    public String getLastnameFirstCharacter() {
        if (this.lastName != null && this.lastName.length() > 0) return this.lastName.substring(0, 1);
        return "";
    }

    public int getPermission(Long permissionId) {
        for (UserPermissionRelation rel : this.permissionRelations) {
            if (rel.permissionType.permission.id.compareTo(permissionId) == 0) {
                return rel.permissionType.value;
            }
        }
        return 0;
    }

    public int getPermissionType(String alias) {
        UserPermissionRelation relation = UserPermissionRelation.find("user.id=?1 AND permissionType.permission.alias=?2", this.id, alias).first();
        if (relation != null) return relation.permissionType.value;
        return 0;
    }
}
