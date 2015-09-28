package controllers.MyClass;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by enkhamgalan on 3/8/15.
 */
public class NotificationJson {

    @Expose
    public List<Long> uid = new ArrayList<Long>();

    @Expose
    public String type;

    @Expose
    public String sender;
    @Expose
    public int senderId;

    public String toJsonString() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

}
