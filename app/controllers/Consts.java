package controllers;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Consts {

    public static final String detailedFormat = "yyyy-MM-dd HH:mm:ss";
    public static final String detailedFormat2 = "yy-MM-dd HH:mm";
    public static final SimpleDateFormat myDateFormat = new SimpleDateFormat(detailedFormat);
    public static final SimpleDateFormat myDateFormat2 = new SimpleDateFormat(detailedFormat2);
    public static final SimpleDateFormat dateFormatMD = new SimpleDateFormat("MM/dd");
    public static final SimpleDateFormat dateFormat2 = new SimpleDateFormat("YYYY/MM/dd");
    public static final String dateFormat = "yyyy-MM-dd";
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
    public static int firstDayOfWeek = Calendar.MONDAY;
    public static final String uploadUserImagePath = "/public/userImages/";
    public static final String uploadDocPath = "/FileCenter/docPath/";
    public static final String uploadRFIAttachPath = "/FileCenter/RFIAttachPath/";
    public static final String uploadPunchListAttachPath = "/FileCenter/PunchListAttachPath/";
    public static final String uploadContractPath = "/FileCenter/Contracts/";
    public static final String uploadMeetingPath = "/FileCenter/Meetings/";
    public static final String uploadEventPath = "/FileCenter/Events/";
    public static final String uploadMeetingTopicPath = "/FileCenter/Meetings/Topic/";
    public static final String uploadDailyLogPath = "/FileCenter/DailyLog/";
    public static final String uploadProjectPath = "/FileCenter/ProjectImage/";
    public static final String uploadDrawingPath = "/FileCenter/DrawingPath/";
    public static final String uploadPostPath = "/FileCenter/PostPath/";
    public static final String uploadDashboardPath = "/FileCenter/Dashboard/";

    public static final int maxDescriptionLength = 10000;
    public static final int maxRiskAssLength = 4000;
    public static final int maxDescriptionLength2 = 8000;

    public static final String imageFileExtensions = "gif,jpg,jpeg,png,bmp,tiff,GIF,JPG,JPEG,PNG,BMP,";
    public static final String imageFileType = "image";
    public static final String imageFileIcon = "/public/images/fileIcon/picture-icon.png";
    public static final String pdfFileExtensions = "pdf,PDF,";
    public static final String pdfFileIcon = "/public/images/fileIcon/pdf-icon.png";
    public static final String wordFileExtensions = "docx,doc,DOCX,DOC,";
    public static final String wordFileIcon = "/public/images/fileIcon/word-icon.png";
    public static final String excelFileExtensions = "xls,xlsx,XLS,XLSX,";
    public static final String excelFileIcon = "/public/images/fileIcon/excel-icon.png";
    public static final String powerPointFileExtensions = "ppt,pptx,PPT,PPTX,";
    public static final String powerPointFileIcon = "/public/images/fileIcon/powerpoint-icon.png";
    public static final String autoCadFileExtensions = "dwg,dxf,dgn,DWG,DXF,DGN";
    public static final String autoCadFileIcon = "/public/images/fileIcon/autocad-icon.png";
    public static final String videoFileExtensions = "avi,wmv,rm,rmvb,mp4,mpeg,mkv,AVI,WMV,RM,RMVB,MP4,MPEG,MKV,";
    public static final String videoFileIcon = "/public/images/fileIcon/video-icon.png";
    public static final String viewableVideoExtensions = "flv,swf,FLV,SWF,";
    public static final String viewableVideoType = "viewableVideo";
    public static final String viewableVideoIcon = "/public/images/fileIcon/viewable-video-icon.png";
    public static final String otherFileIcon = "/public/images/fileIcon/other-icon.png";

    public static final String permissionDashboard = "dashboard";
    public static final String permissionGantt = "gantt";
    public static final String permissionMyPlan = "myPlan";
    public static final String permissionDailyLog = "dailyLog";
    public static final String permissionMail = "mail";
    public static final String permissionRFI = "rfi";
    public static final String permissionGreatePunchList = "punchlist";
    public static final String permissionReport = "report";
    public static final String permissionMeeting = "meeting";
    public static final String permissionMonitorDrawing = "drawing";
    public static final String permissionGallery = "gallery";
    public static final String permissionFileShare = "file";
    public static final String permissionEvent = "event";
    public static final String permissionBudget = "budget";
    public static final String permissionContract = "contract";
    public static final String permissionInventory = "inventory";
    public static final String permissionMap = "map";
    public static final String permissionForum = "forum";
    public static final String permissionAccount = "account";
    public static final String permissionAdmin = "admin";
}
