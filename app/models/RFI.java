package models;

import controllers.CRUD;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Rina on 1/11/2015.
 */
@Entity(name = "tb_rfi")
public class RFI extends Model {

    @Required
    @Column(length = 65535)
    public String subject;

    @Required
    @ManyToOne
    public User assignee;

    @Required
    public Date createDate;

    @Required
    public Date dueDate;

    public Date closedDate;

    @Column(length = 65535)
    public String question;

    @Column(length = 65535)
    public String location;

    public boolean private_;

    public Long tempId;

    @CRUD.Hidden
    @ManyToOne
    public Task task;

    @CRUD.Hidden
    @ManyToOne
    public Project project;

    @CRUD.Hidden
    @ManyToOne
    public RFI_Impact scheduleImpact;

    @CRUD.Hidden
    @ManyToOne
    public RFI_Impact costImpact;

    public boolean overdueNotification;

    @Required
    @ManyToOne
    public User questionReceivedFrom;

    @CRUD.Hidden
    @ManyToOne
    public RFI_Status rfi_status;

    @CRUD.Hidden
    @OneToMany(mappedBy = "rfi", cascade = CascadeType.ALL)
    public List<RFI_Forward> rfi_forwards;

    @CRUD.Hidden
    @OneToMany(mappedBy = "rfi", cascade = CascadeType.ALL)
    public List<DrawingPin> drawing_pins;

    @CRUD.Hidden
    @OneToMany(mappedBy = "rfi", cascade = CascadeType.ALL)
    public List<RFI_Distribution> distributions;

    @CRUD.Hidden
    @OneToMany(mappedBy = "rfi", cascade = CascadeType.ALL)
    public List<RFI_Attach> rfi_attaches;

    @CRUD.Hidden
    @OneToMany(mappedBy = "rfi", cascade = CascadeType.ALL)
    public List<RFI_Tracking> trackings;


    public boolean isRelated(Long id){
        if(assignee.id.equals(id)) return true;
        if(questionReceivedFrom.id.equals(id)) return true;
        if(distributions != null){
            for (int i = 0; i < distributions.size(); i++)
                if(distributions.get(i).getId().equals(id))
                    return true;
        }
        if(!private_) return true;
        return false;
    }
}
