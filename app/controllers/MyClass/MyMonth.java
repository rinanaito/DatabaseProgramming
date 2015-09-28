package controllers.MyClass;

import java.util.Date;

/**
 * Created by enkhamgalan on 12/28/14.
 */
public class MyMonth {
    public int merge;
    public int month;
    public String name;
    public Date date;

    public MyMonth(String name, int month, int merge, Date date) {
        this.name = name;
        this.month = month;
        this.merge = merge;
        this.date = date;
    }
}
