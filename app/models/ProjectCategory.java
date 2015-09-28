package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Created by Enkh-Amgalan on 3/21/15.
 */
@Entity(name = "tb_project_category")
public class ProjectCategory extends Model {
    @Required
    public String category;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    public List<Portfolio> portfolios;
}
