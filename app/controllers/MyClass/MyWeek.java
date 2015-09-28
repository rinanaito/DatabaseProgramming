package controllers.MyClass;

import java.util.Date;

/**
 * Created by enkhamgalan on 12/28/14.
 */
public class MyWeek {
    public int year;
    public int week;
    public Date date;
    public String name;

    public MyWeek(String name, Date date, int week, int year) {
        this.name = name;
        this.date = date;
        this.week = week;
        this.year = year;
    }
}
