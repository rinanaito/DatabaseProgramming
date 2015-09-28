package controllers;

import play.mvc.Controller;
import play.mvc.With;

/**
 * Created by enkhamgalan on 1/22/15.
 */
@With(Secure.class)
public class Dashboard extends Controller {
    public static void index() {
        String navbar="Dashboard";
        render(navbar);
    }
}
