package models;

import play.data.validation.Max;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created by enkhamgalan on 3/14/15.
 */
@Entity(name = "tb_percenthistory_object")
public class PercentHistoryObject extends Model {

    public Date date;

    public int timeType;

    @Max(100)
    public Float completedPercent = 0.0f;

    @ManyToOne
    public ProjectObject projectObject;
}
