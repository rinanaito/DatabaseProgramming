package models;

import controllers.CRUD;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Created by Rina on 1/12/2015.
 */
@Entity(name = "tb_rfi_impact")
public class RFI_Impact extends Model{
    public String impact;

    @CRUD.Hidden
    @OneToMany(mappedBy = "scheduleImpact", cascade = CascadeType.ALL)
    public List<RFI> rfis;

    @CRUD.Hidden
    @OneToMany(mappedBy = "costImpact", cascade = CascadeType.ALL)
    public List<RFI> rfiList;
}
