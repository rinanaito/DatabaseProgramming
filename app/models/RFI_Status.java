package models;

import controllers.CRUD;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.List;

/**
 * Created by Rina on 1/11/2015.
 */
@Entity(name = "tb_rfi_status")
public class RFI_Status extends Model {
    @Required
    public String status;

    @CRUD.Hidden
    @OneToMany(mappedBy = "rfi_status", cascade = CascadeType.ALL)
    public List<RFI> rfis;

    @Transient
    public float count;

}
