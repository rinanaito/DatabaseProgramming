package controllers;

import org.apache.commons.io.IOUtils;
import play.Play;
import play.mvc.Controller;
import play.mvc.With;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;

@With(Secure.class)
public class FileUploader extends Controller {
    public static void uploadFile(String uploadPath, String filename) throws Exception {
        String extension = "null";
        String FileNameOrig = "";
        boolean success = true;
        if (request.isNew) {
            FileOutputStream moveTo = null;

            try {
                String[] length = filename.split("\\.");
                FileNameOrig = length[0];
                extension = length[length.length - 1];
                Date currentDate = new Date();
                filename = currentDate.getTime() + "";

                InputStream data = request.body;
                uploadPath = uploadPath + Consts.simpleDateFormat.format(currentDate);

                String absolutePath = Functions.cleanUrl(Play.applicationPath.getAbsolutePath() + "/" + uploadPath);

                File f = new File(absolutePath);
                if (!f.exists()) {
                    if (!f.mkdirs()) throw new Exception("Unable to create upload dir");
                }
                absolutePath = Functions.cleanUrl(absolutePath + "/" + filename + "." + extension);
                moveTo = new FileOutputStream(new File(absolutePath));

                IOUtils.copy(data, moveTo);
                moveTo.close();
                if (isImage(extension)) {        //to small
                    ConvertToImage convertToImage = new ConvertToImage();
//                    if (!convertToImage.convert(uploadPath + "/" + filename, extension, 1200, 1200, true, false))
                    ;
                    if (!convertToImage.convert(uploadPath + "/" + filename, extension, 400, 400, false, true))
                        ;
                }
                uploadPath += "/" + filename;
            } catch (Exception ex) {
                ex.printStackTrace();
                if (moveTo != null)
                    moveTo.close();
                success = false;
            }
        }
        renderJSON("{success: " + success + ",filedir:'" + uploadPath + "',filename:'" + FileNameOrig + "',extension:'" + extension + "'}");
    }

    public static boolean isImage(String ext) {
        return (Consts.imageFileExtensions.contains(ext));
    }

    public static void deleteUploadFile(String fileDir, String extension) {
        try {
            File file = new File(Play.applicationPath.getAbsolutePath() + fileDir + "." + extension);
            if (file.exists()) file.delete();
            if (isImage(extension)) {
                file = new File(Play.applicationPath.getAbsolutePath() + fileDir + "t.jpg");
                if (file.exists()) file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void uploadFileCustom(String filename, String uploadPath, String upfilename, String ratio) throws Exception {
        String fileExtension, uploadedFileName = "", fileexen;
        int rw, rh;
        rw = Integer.parseInt(ratio.split("x")[0]);
        rh = Integer.parseInt(ratio.split("x")[1]);
        boolean success = true;
        if (request.isNew) {
            FileOutputStream moveTo = null;
            try {
                InputStream data = request.body;
                String[] length = filename.split("\\.");
                fileexen = length[length.length - 1];
                filename = upfilename;
                fileExtension = "jpg";
                uploadedFileName = Play.applicationPath.getAbsolutePath() + uploadPath + filename + "." + fileexen;
                moveTo = new FileOutputStream(new File(uploadedFileName));
                IOUtils.copy(data, moveTo);
                moveTo.close();
                ConvertToImage convertToImage = new ConvertToImage();
                if (!convertToImage.convert(uploadPath + filename, fileexen, rw, rh, false, false))
                    new File(Play.applicationPath.getAbsolutePath() + uploadPath + filename + "." + fileexen).delete();
                if (!fileexen.endsWith("jpg"))
                    new File(Play.applicationPath.getAbsolutePath() + uploadPath + filename + "." + fileexen).delete();
                filename = filename + "." + fileExtension;
            } catch (Exception ex) {
                success = false;
                ex.printStackTrace();
                if (moveTo != null)
                    moveTo.close();
            }
        }
        renderJSON("{success: " + success + ",filedir:'" + uploadPath + "',filename:'" + filename + "'}");
    }
}
