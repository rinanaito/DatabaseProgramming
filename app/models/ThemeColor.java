package models;

import controllers.CRUD;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

/**
 * Created by enkhamgalan on 2/25/15.
 */
@Entity(name = "tb_themecolor")
public class ThemeColor extends Model {

    @Required
    String color;

    @CRUD.Hidden
    @OneToMany(mappedBy = "themeColor", cascade = CascadeType.ALL)
    public List<AccountSetting> accountSettings;
}
