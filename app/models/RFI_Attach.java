package models;

import controllers.CRUD;
import play.data.validation.Required;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Created by Rina on 1/11/2015.
 */
@Entity(name = "tb_rfi_attach")
public class RFI_Attach extends ModelAttach {
    @Required
    public String name;
    @Required
    public String dir;

    @Required
    public String extension;

    @ManyToOne
    @CRUD.Hidden
    public RFI rfi;

    @ManyToOne
    @CRUD.Hidden
    public RFI_Tracking tracking;

    @CRUD.Hidden
    @OneToMany(mappedBy = "rfiAttachDrawing", cascade = CascadeType.ALL)
    public List<DrawingPin> drawing_pins;
}
