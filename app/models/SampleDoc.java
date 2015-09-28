package models;

import controllers.CRUD;
import controllers.Consts;
import play.data.validation.Max;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by enkhamgalan on 12/20/14.
 */
@Entity
public class SampleDoc extends Model {

    @CRUD.Hidden
    public Date createdDate;

    @Required
    @Column(length = 65535)
    public String documentText;

    @CRUD.Hidden
    @OneToMany(mappedBy = "sampleDoc", cascade = CascadeType.ALL)
    public List<SampleDocAttach> docAttaches;
}
