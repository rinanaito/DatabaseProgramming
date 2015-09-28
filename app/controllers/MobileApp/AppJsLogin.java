package controllers.MobileApp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import models.User;

import java.util.List;

/**
 * Created by enkhamgalan on 4/5/15.
 */
public class AppJsLogin {

    @Expose
    public Long logined=0L;

    @Expose
    public String uid="";

    public String toJsonString() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }
}
