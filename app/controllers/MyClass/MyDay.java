package controllers.MyClass;

import java.util.Date;

/**
 * Created by enkhamgalan on 12/28/14.
 */
public class MyDay {
    String day;
    String dayName;
    boolean weekend = false;
    Date date;

    public MyDay(Date date, String day, String dayName, int day_of_week) {
        this.date = date;
        this.day = day;
        this.dayName = dayName;
        if (day_of_week == 1 || day_of_week == 7) this.weekend = true;
    }
}
