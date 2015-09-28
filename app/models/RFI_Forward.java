package models;

import controllers.CRUD;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

/**
 * Created by Rina on 1/11/2015.
 */
@Entity(name = "tb_rfi_forward")
public class RFI_Forward extends Model {

    @Required
    @ManyToOne
    @CRUD.Hidden
    public RFI rfi;

    @Required
    public Date createDate;

    public String description;

    @CRUD.Hidden
    @OneToMany(mappedBy = "forward", cascade = CascadeType.ALL)
    public List<RFI_Distribution> distributions;

}
