package models;

import controllers.CRUD;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Rina on 1/13/2015.
 */
@Entity(name = "tb_rfi_tracking")
public class RFI_Tracking extends Model {
    @Required
    @CRUD.Hidden
    @ManyToOne
    public User author;

    @Required
    public Date createDate;

    @Column(length = 65535)
    public String note;

    public Long tempId;

    @Required
    @CRUD.Hidden
    @ManyToOne
    public RFI rfi;

    @OneToMany(mappedBy = "tracking", cascade = CascadeType.ALL)
    public List<RFI_Attach> tracking_attaches;

    @CRUD.Hidden
    @OneToMany(mappedBy = "rfi_tracking", cascade = CascadeType.ALL)
    public List<DrawingPin> drawing_pins;


}
