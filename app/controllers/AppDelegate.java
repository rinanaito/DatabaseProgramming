package controllers;

import com.google.gson.*;
import controllers.MobileApp.AppConsts;
import models.*;
import org.apache.commons.io.IOUtils;
import play.Play;
import play.mvc.Controller;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by enkhamgalan on 4/5/15.
 */
public class AppDelegate extends Controller {
    public static void login(String body) {
//        JsonObject json = (JsonObject) new JsonParser().parse(params.get("body"));
        if (body != null) {
            JsonObject json = (JsonObject) new JsonParser().parse(body);
            User us = User.find("username=?1 and password=?2", json.get("username").getAsString().toUpperCase(), Functions.getSha1String(json.get("password").getAsString())).first();
            if (us != null) {
                JsonObject obj = new JsonObject();
                obj.add("session", new JsonPrimitive(us.id + "-" + us.username + "-" + us.password));
                JsonObject usobj = new JsonObject();
                usobj.addProperty("id", us.id);
                usobj.addProperty("username", us.username);
                obj.add("user", usobj);
                renderJSON(obj);
            }
        }
    }

    public static void portfolios(String body) {
        JsonObject json = (JsonObject) new JsonParser().parse(body);
        User user = getUser(json.get("session").getAsString());
        if (user != null) {
            List<ProjectAssignRel> rels = ProjectAssignRel.find("user.id=?1", user.id).fetch();
            JsonArray obj = new JsonArray();
            for (ProjectAssignRel rel : rels) {
                JsonObject object = new JsonObject();
                object.addProperty("id", rel.project.id);
                object.addProperty("name", rel.project.name);
                object.addProperty("location", rel.project.portfolio.location);
                if (rel.project.portfolio.isActive) object.addProperty("stage", "Идэвхтэй");
                else object.addProperty("stage", "Идэвхгүй");
                object.addProperty("category", rel.project.portfolio.category.category);
                obj.add(object);
            }
            renderJSON(obj);
        }
    }

    public static void portfolioUsers(String body) {
        JsonObject json = (JsonObject) new JsonParser().parse(body);
        User user = getUser(json.get("session").getAsString());
        if (user != null) {
            Long pid = json.get("projectid").getAsLong();
            List<ProjectAssignRel> rels = ProjectAssignRel.find("project.id=?1", pid).fetch();
            JsonArray obj = new JsonArray();
            for (ProjectAssignRel rel : rels) {
                JsonObject object = new JsonObject();
                object.addProperty("id", rel.user.id);
                object.addProperty("username", rel.user.toString());
                object.addProperty("team", rel.user.userTeam.name);
                object.addProperty("position", rel.user.userPosition.name);
                object.addProperty("mobile", rel.user.phone1);
                object.addProperty("mail", rel.user.email);
                object.addProperty("projectid", pid);
                obj.add(object);
            }
            renderJSON(obj);
        }
    }

    public static User getUser(String session) {
        String[] usData = session.split("-");
        return User.find("id=?1 AND username=?2 AND password=?3", Long.parseLong(usData[0]), usData[1], usData[2]).first();
    }

    public static void RFIList(String body) {
        JsonObject json = (JsonObject) new JsonParser().parse(body);
        User user = getUser(json.get("session").getAsString());

        if (user != null) {
            Long pid = json.get("projectid").getAsLong();
            UserTeam userTeam = user.userTeam;
            List<RFI_Distribution> user_distributions = user.rfi_distributions;
            user_distributions.addAll(userTeam.rfi_distributions);
            String query = "project.id =" + pid + " and (";
            for (int i = 0; i < user_distributions.size(); i++) {
                if (user_distributions.get(i).rfi != null) {
                    if (!user_distributions.get(i).rfi.rfi_status.status.equalsIgnoreCase("Draft"))
                        query += "id = " + user_distributions.get(i).rfi.id + " or ";
                }
            }
            query += "not(questionReceivedFrom.id <> " + user.id + " and assignee.id <> " + user.id + " and private_ = true))";
            List<RFI> rfis = RFI.find(query).fetch();
            JsonArray obj = new JsonArray();
            for (RFI rfi : rfis) {
                JsonObject object = new JsonObject();
                object.addProperty("assignee", rfi.assignee.id);
                object.addProperty("assigneeName", rfi.assignee.getLastnameFirstCharacter() + ". " + rfi.assignee.firstName);
                object.addProperty("balanceCost", rfi.costImpact.impact.equalsIgnoreCase("Yes") ? "Тийм" : (rfi.costImpact.impact.equalsIgnoreCase("No") ? "Үгүй" : "Мэдэхгүй"));
                object.addProperty("date", rfi.createDate.getTime());
                object.addProperty("desc", rfi.question);
                JsonArray distrib = new JsonArray();
                String id;
                for (RFI_Distribution dis : rfi.distributions) {
                    if (dis.code.equals("t")) {
                        for (User usr : dis.userTeam.users) {
                            id = String.valueOf(usr.id);
                            distrib.add(new JsonPrimitive(id));
                        }
                    } else {
                        id = String.valueOf(dis.user.id);
                        distrib.add(new JsonPrimitive(id));
                    }
                }
                object.add("distribution", distrib);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                object.addProperty("dueDate", dateFormat.format(rfi.dueDate));
                JsonArray files = new JsonArray();
                for (RFI_Attach attach : rfi.rfi_attaches) {
                    String[] str = attach.dir.split("/");
                    files.add(new JsonPrimitive(str[str.length-1] + "." + attach.extension));
                }
                object.add("filenames", files);
                object.addProperty("fromUserId", rfi.questionReceivedFrom.id);
                object.addProperty("fromUserName", rfi.questionReceivedFrom.getLastnameFirstCharacter() + ". " + rfi.questionReceivedFrom.firstName);
                object.addProperty("graphCost", rfi.scheduleImpact.impact.equalsIgnoreCase("Yes") ? "Тийм" : (rfi.scheduleImpact.impact.equalsIgnoreCase("No") ? "Үгүй" : "Мэдэхгүй"));
                object.addProperty("id", rfi.id);
                object.addProperty("imageNumber", "aaaa");
                object.addProperty("isPrivate", rfi.private_ ? 1 : 0);
                object.addProperty("location", getString(rfi.location));
                object.addProperty("number", rfi.id);
                object.addProperty("open", rfi.rfi_status.status.equals("Open") ? 0 : 1);
                object.addProperty("overdueNotification", rfi.overdueNotification ? 1 : 0);
                object.addProperty("question", getString(rfi.subject));
                object.addProperty("tempId", rfi.tempId);
                object.addProperty("projectid", pid);
                obj.add(object);
            }
            renderJSON(obj);
        }

    }

    public static void RFIReplyList(String body) {
        JsonObject json = (JsonObject) new JsonParser().parse(body);
        User user = getUser(json.get("session").getAsString());

        if (user != null) {
            List<RFI_Tracking> trackings = RFI_Tracking.find("rfi.id = " + json.get("rfiid").getAsLong()).fetch();
            JsonArray obj = new JsonArray();
            for (RFI_Tracking track : trackings) {
                JsonObject object = new JsonObject();
                object.addProperty("id", track.id);
                object.addProperty("date", track.createDate.getTime());
                object.addProperty("rfiId", track.rfi.id);
                object.addProperty("username", track.author.getLastnameFirstCharacter() + "." + track.author.firstName);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                JsonArray files = new JsonArray();
                for (RFI_Attach attach : track.tracking_attaches) {
                    String[] str = attach.dir.split("/");
                    files.add(new JsonPrimitive(str[str.length-1] + "." + attach.extension));
                }
                object.add("filenames", files);
                object.addProperty("text", track.note);
                object.addProperty("tempId", track.tempId);
                obj.add(object);
            }
            System.out.println(obj.toString());
            renderJSON(obj);
        }
    }

    public static void RFIAttachRedirect(String body) {
        String[] name = body.split("\\.");
        RFI_Attach attach = RFI_Attach.find("dir like '%" + name[0] + "'").first();
        redirect(attach.dir + "." + attach.extension);
    }

    public static void RFINew(String body) throws ParseException {
        JsonObject json = (JsonObject) new JsonParser().parse(body);
        User user = getUser(json.get("session").getAsString());
        if (user != null) {
            JsonObject rfiDetails = json.get("rfi").getAsJsonObject();
            Long pid = rfiDetails.get("projectid").getAsLong();
            int s = RFI.find("tempId = " + rfiDetails.get("tempid").getAsLong()).fetch().size();
            if (s == 0) {
                RFI rfi = new RFI();
                rfi.question = rfiDetails.get("desc").getAsString();
                rfi.subject = rfiDetails.get("question").getAsString();
                rfi.questionReceivedFrom = User.findById(rfiDetails.get("fromuserid").getAsLong());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                String dater = rfiDetails.get("duedate").getAsString();
                rfi.dueDate = dateFormat.parse(dater);
                rfi.createDate = new Date();
                rfi.tempId = rfiDetails.get("tempid").getAsLong();
                rfi.project = Project.findById(pid);
                rfi.assignee = User.findById(rfiDetails.get("assignee").getAsLong());
                rfi.location = rfiDetails.get("location").getAsString();
                String cost = rfiDetails.get("graphcost").getAsString();
                rfi.scheduleImpact = RFI_Impact.find("impact = '" + (cost.equals("Тийм") ? "Yes" : (cost.equals("Үгүй") ? "No" : "Unknown")) + "'").first();
                cost = rfiDetails.get("balancecost").getAsString();
                rfi.costImpact = RFI_Impact.find("impact = '" + (cost.equals("Тийм") ? "Yes" : (cost.equals("Үгүй") ? "no" : "Unknown")) + "'").first();
                rfi.private_ = (rfiDetails.get("isprivate").getAsString().equalsIgnoreCase("0") ? false : true);
                rfi.overdueNotification = (rfiDetails.get("overduenotification").getAsString().equalsIgnoreCase("0") ? false : true);
                rfi.rfi_status = RFI_Status.find("status = '" + (rfiDetails.get("open").getAsString().equalsIgnoreCase("0") ? "Open" : "Closed") + "'").first();
                rfi.create();
                JsonArray filenames = rfiDetails.get("filenames").getAsJsonArray();
                Date currentDate = new Date();
                if (filenames.size() > 0) {
                    String uploadPath = Consts.uploadRFIAttachPath;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(currentDate);
                    uploadPath += calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE) + "/";
                    for (int i = 0; i < filenames.size(); i++) {
                        JsonPrimitive ids = filenames.get(i).getAsJsonPrimitive();
                        RFI_Attach attach = new RFI_Attach();
                        String[] file = ids.getAsString().split("\\.");
                        attach.dir = uploadPath + file[0];
                        attach.name = file[0];
                        attach.extension = file[1];
                        attach.rfi = rfi;
                        attach.create();
                    }
                }
                JsonArray distrib = rfiDetails.get("distribution").getAsJsonArray();
                for (int i = 0; i < distrib.size(); i++) {
                    JsonPrimitive ids = distrib.get(i).getAsJsonPrimitive();
                    RFI_Distribution distribution = new RFI_Distribution("u", rfi, ids.getAsLong());
                    distribution.create();
                }
                JsonObject object = new JsonObject();
                object.addProperty("newId", rfi.id);
                object.addProperty("date", rfi.createDate.getTime());
                object.addProperty("tempId", rfi.tempId);
                object.addProperty("number", rfi.id);
                renderJSON(object);
            } else {
                RFI rfi = RFI.find("tempId = " + rfiDetails.get("tempid").getAsLong()).first();
                rfi.question = rfiDetails.get("desc").getAsString();
                rfi.subject = rfiDetails.get("question").getAsString();
                rfi.questionReceivedFrom = User.findById(rfiDetails.get("fromuserid").getAsLong());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                String dater = rfiDetails.get("duedate").getAsString();
                rfi.dueDate = dateFormat.parse(dater);
                rfi.createDate = new Date();
                rfi.tempId = rfiDetails.get("tempid").getAsLong();
                rfi.project = Project.findById(pid);
                rfi.assignee = User.findById(rfiDetails.get("assignee").getAsLong());
                rfi.location = rfiDetails.get("location").getAsString();
                String cost = rfiDetails.get("graphcost").getAsString();
                rfi.scheduleImpact = RFI_Impact.find("impact = '" + (cost.equals("Тийм") ? "Yes" : (cost.equals("Үгүй") ? "No" : "Unknown")) + "'").first();
                cost = rfiDetails.get("balancecost").getAsString();
                rfi.costImpact = RFI_Impact.find("impact = '" + (cost.equals("Тийм") ? "Yes" : (cost.equals("Үгүй") ? "No" : "Unknown")) + "'").first();
                rfi.private_ = (rfiDetails.get("isprivate").getAsString().equalsIgnoreCase("0") ? false : true);
                rfi.overdueNotification = (rfiDetails.get("overduenotification").getAsString().equalsIgnoreCase("0") ? false : true);
                rfi.rfi_status = RFI_Status.find("status = '" + (rfiDetails.get("open").getAsString().equalsIgnoreCase("0") ? "Open" : "Closed") + "'").first();
                rfi.save();
                for (RFI_Attach attach : rfi.rfi_attaches) {
                    attach._delete();
                }
                rfi.rfi_attaches = new ArrayList<RFI_Attach>();
                rfi.save();
                JsonArray filenames = rfiDetails.get("filenames").getAsJsonArray();
                Date currentDate = new Date();
                if (filenames.size() > 0) {
                    String uploadPath = Consts.uploadRFIAttachPath;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(currentDate);
                    uploadPath += calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE) + "/";
                    for (int i = 0; i < filenames.size(); i++) {
                        JsonPrimitive ids = filenames.get(i).getAsJsonPrimitive();
                        RFI_Attach attach = new RFI_Attach();
                        String[] file = ids.getAsString().split("\\.");
                        attach.dir = uploadPath + file[0];
                        attach.name = file[0];
                        attach.extension = file[1];
                        attach.rfi = rfi;
                        attach.create();
                    }
                }
                for (RFI_Distribution dist : rfi.distributions) {
                    dist._delete();
                }
                rfi.distributions = new ArrayList<RFI_Distribution>();
                rfi.save();
                JsonArray distrib = rfiDetails.get("distribution").getAsJsonArray();
                for (int i = 0; i < distrib.size(); i++) {
                    JsonPrimitive ids = distrib.get(i).getAsJsonPrimitive();
                    RFI_Distribution distribution = new RFI_Distribution("u", rfi, ids.getAsLong());
                    distribution.create();
                }
                JsonObject object = new JsonObject();
                object.addProperty("newId", rfi.id);
                object.addProperty("date", rfi.createDate.getTime());
                object.addProperty("tempId", rfi.tempId);
                object.addProperty("number", rfi.id);
                renderJSON(object);
            }
        }
    }

    public static void RFIReplyNew(String body) {
        JsonObject json = (JsonObject) new JsonParser().parse(body);
        User user = getUser(json.get("session").getAsString());
        if (user != null) {
            JsonObject rfiReply = json.get("rfiReply").getAsJsonObject();
            int s = RFI_Tracking.find("tempId = " + rfiReply.get("tempid").getAsLong()).fetch().size();
            if (s == 0) {
                RFI_Tracking track = new RFI_Tracking();
                track.note = rfiReply.get("text").getAsString();
                track.author = User.find("username = '" + rfiReply.get("username").getAsString() + "'").first();
                track.createDate = new Date();
                track.tempId = rfiReply.get("tempid").getAsLong();
                track.rfi = RFI.findById(rfiReply.get("rfiid").getAsLong());
                track.create();
                JsonArray filenames = rfiReply.get("filenames").getAsJsonArray();
                Date currentDate = new Date();
                if (filenames.size() > 0) {
                    String uploadPath = Consts.uploadRFIAttachPath;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(currentDate);
                    uploadPath += calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE) + "/";
                    for (int i = 0; i < filenames.size(); i++) {
                        JsonPrimitive ids = filenames.get(i).getAsJsonPrimitive();
                        RFI_Attach attach = new RFI_Attach();
                        String[] file = ids.getAsString().split("\\.");
                        attach.dir = uploadPath + file[0];
                        attach.name = file[0];
                        attach.extension = file[1];
                        attach.tracking = track;
                        attach.create();
                    }
                }
                JsonObject object = new JsonObject();
                object.addProperty("newId", track.id);
                object.addProperty("date", track.createDate.getTime());
                object.addProperty("tempId", track.tempId);
                renderJSON(object);
            } else {
                RFI_Tracking track = RFI_Tracking.find("tempId = " + rfiReply.get("tempid").getAsLong()).first();
                track.note = rfiReply.get("text").getAsString();
                track.author = User.find("username = '" + rfiReply.get("username").getAsString() + "'").first();
                track.createDate = new Date();
                track.tempId = rfiReply.get("tempid").getAsLong();
                track.rfi = RFI.findById(rfiReply.get("rfiid").getAsLong());
                track.save();
                JsonArray filenames = rfiReply.get("filenames").getAsJsonArray();
                Date currentDate = new Date();
                for (RFI_Attach attach : track.tracking_attaches) {
                    attach._delete();
                }
                track.tracking_attaches = new ArrayList<RFI_Attach>();
                track.save();
                if (filenames.size() > 0) {
                    String uploadPath = Consts.uploadRFIAttachPath;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(currentDate);
                    uploadPath += calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE) + "/";
                    for (int i = 0; i < filenames.size(); i++) {
                        JsonPrimitive ids = filenames.get(i).getAsJsonPrimitive();
                        RFI_Attach attach = new RFI_Attach();
                        String[] file = ids.getAsString().split("\\.");
                        attach.dir = uploadPath + file[0];
                        attach.name = file[0];
                        attach.extension = file[1];
                        attach.tracking = track;
                        attach.create();
                    }
                }
                JsonObject object = new JsonObject();
                object.addProperty("newId", track.id);
                object.addProperty("date", track.createDate.getTime());
                object.addProperty("tempId", track.tempId);
                renderJSON(object);
            }
        }
    }

    public static void Android(String body) {
        System.out.println(body);
//        JsonObject json = (JsonObject) new JsonParser().parse(body);
//        User user = getUser(json.get("session").getAsString());

    }

    public static void RFIUploadAttach() throws Exception {
        String filename = request.headers.get("filename").toString();
        System.out.println(filename);
        String uploadPath = Consts.uploadRFIAttachPath;
        String extension = "null";
        boolean success = true;
        if (request.isNew) {
            FileOutputStream moveTo = null;

            try {
                filename = filename.replace("[", "").replace("]", "");
                String[] filer = filename.split("\\.");
                extension = filer[filer.length - 1];
                Date currentDate = new Date();
                filename = currentDate.getTime() + "";
                InputStream data = request.body;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate);
                uploadPath += calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE) + "/";
                String absolutePath = Functions.cleanUrl(Play.applicationPath.getAbsolutePath() + "/" + uploadPath);
                File f = new File(absolutePath);
                if (!f.exists()) {
                    if (!f.mkdirs()) throw new Exception("Unable to create upload dir");
                }
                absolutePath = Functions.cleanUrl(absolutePath + "/" + filer[0] + "." + extension);
                moveTo = new FileOutputStream(new File(absolutePath));
                IOUtils.copy(data, moveTo);
                moveTo.close();
                if (FileUploader.isImage(extension)) {        //to small
                    ConvertToImage convertToImage = new ConvertToImage();
//                    if (!convertToImage.convert(uploadPath + "/" + filename, extension, 1200, 1200, true, false));
                    if (!convertToImage.convert(uploadPath + "/" + filer[0], extension, 400, 400, false, true)) ;
                }
                uploadPath += "/" + filename;
            } catch (Exception ex) {
                ex.printStackTrace();
                if (moveTo != null)
                    moveTo.close();
                success = false;
            }
        }
    }

    public static String getString(String str) {
        if (str == null)
            return "";
        return str;
    }
}
