package controllers;

import controllers.MyClass.MyWeekVal;
import org.apache.commons.codec.binary.Base64;
import play.Play;
import play.classloading.enhancers.LocalvariablesNamesEnhancer;
import play.db.jpa.Model;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Functions {
    public static String[] monthName = {"Нэгдүгээр сар", "Хоёрдугаар cар", "Гуравдугаар сар", "Дөрөвдүгээр сар", "Тавдугаар сар", "Зургаадугаар сар", "Долоодугаар сар", "Наймдугаар сар", "Есдүгээр сар", "Аравдугаар сар", "Арваннэгдүгээр сар", "Арванхоёрдугаар сар"};
    public static String[] monthNameShort = {"1сар", "2cар", "3сар", "4сар", "5сар", "6сар", "7сар", "8сар", "9сар", "10сар", "11сар", "12сар"};
    public static String[] dayNames = {"Ням", "Даваа", "Мягмар", "Лхагва", "Пүрэв", "Баасан", "Бямба"};
    public static String[] dayName2 = {"Даваа", "Мягмар", "Лхагва", "Пүрэв", "Баасан", "Бямба", "Ням"};
    public static String[] dayNamesMin = {"Ня", "Да", "Мя", "Лх", "Пү", "Ба", "Бя"};
    public static String[] dayNamesShort = {"Ням", "Дав", "Мяг", "Лха", "Пүр", "Баа", "Бям"};

    public static String[] fileShareExtensions = {"Бүгд", "Зураг", "CAD", "PDF", "Office"};
    public static String[] fileShareExtensionTypes = {"gif,jpg,jpeg,png,bmp,tiff,dwg,dxf,dgn,pdf,docx,doc,xls,xlsx,ppt,pptx"
            , "gif,jpg,jpeg,png,bmp,tiff,", "dwg,dxf,dgn,", "pdf", "docx,doc,xls,xlsx,ppt,pptx"};

    public static String getMonthName(Date date) {
        String strDateFormat = "MMMM";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        return sdf.format(date);
    }

    public static String getDateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return Functions.monthName[calendar.get(Calendar.MONTH)] + " " + calendar.get(Calendar.DATE) + " " + Functions.dayNames[calendar.get(Calendar.DAY_OF_WEEK) - 1] + ", " + new SimpleDateFormat("HH").format(date) + ':' + new SimpleDateFormat("mm").format(date);
    }

    public static Calendar setCalDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTimeZone(TimeZone.getTimeZone("Asia/Ulaanbaatar"));
        return cal;
    }

    public static ArrayList<Long> ids;

    public static List<?> subtractEntityLists(List<?> list1, List<?> list2) {
        for (Model model2 : (List<Model>) list2)
            for (Model model1 : (List<Model>) list1)
                if (model1.id.longValue() == model2.id.longValue()) {
                    list1.remove(model1);
                    break;
                }
        return list1;
    }

    public static String[] CalendarList(String dateVal, int action) {

        int mm = Integer.parseInt(dateVal.split("-")[1]) - 1;
        int yy = 0;
        String cals[] = new String[8];
        int day;
        if (action == 0) day = Integer.parseInt(dateVal.split("-")[2]);
        else {
            if (action == 1 && mm == 12) {
                yy = 1;
                mm = 0;
            }
            if (action == -1 && mm == 1) {
                yy = -1;
                mm = 13;
            }
            day = 1;
        }
        Date date = new Date(Integer.parseInt(dateVal.split("-")[0]) - 1900 + yy,
                mm + action,
                Integer.parseInt(dateVal.split("-")[2]));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cals[0] = cal.get(Calendar.DATE) + "";
        if (action == 1) cals[0] = "1";
        if (action == -1) cals[0] = cal.getActualMaximum(Calendar.DAY_OF_MONTH) + "";
        cals[1] = (cal.get(Calendar.MONTH) + 1) + "";
        cals[2] = cal.get(Calendar.YEAR) + "";
        cals[3] = cal.getActualMaximum(Calendar.DAY_OF_MONTH) + "";
        if ((cal.get(Calendar.MONTH) - 1) < 0) cals[4] = monthName[11];
        else cals[4] = monthName[cal.get(Calendar.MONTH) - 1];
        cals[5] = monthName[cal.get(Calendar.MONTH)];
        if ((cal.get(Calendar.MONTH) + 1) > 11) cals[6] = monthName[0];
        else cals[6] = monthName[cal.get(Calendar.MONTH) + 1];
        return cals;
    }

    public static Date PrevNextDay(Date date, int prevNext) {
        Calendar origDay = Calendar.getInstance();
        origDay.setTime(date);
        Calendar nextDay = (Calendar) origDay.clone();
        if (prevNext > 0) nextDay.add(Calendar.DAY_OF_YEAR, 1);
        else nextDay.add(Calendar.DAY_OF_YEAR, -1);
        return nextDay.getTime();
    }

    public static Date addOrMinusDays(Date incomingDate, int dayCount, boolean add) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(incomingDate);
        if (add) {
            cal.add(Calendar.DATE, +dayCount); //minus number would decrement the days
        } else {
            cal.add(Calendar.DATE, -dayCount); //minus number would decrement the days
        }
        return cal.getTime();
    }

    public static Date addOrMinusMonth(Date incomingDate, int monthCount, boolean add) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(incomingDate);
        if (add) {
            cal.add(Calendar.MONTH, +monthCount); //minus number would decrement the days
        } else {
            cal.add(Calendar.MONTH, -monthCount); //minus number would decrement the days
        }
        return cal.getTime();
    }

    public static Date StringToDate(String text) {
        String sss = text.split("-")[2];
        String ss = sss.split(" ")[1];
        return new Date(2000 + Integer.parseInt(text.split("-")[0]) - 1900, Integer.parseInt(text.split("-")[1]) - 1,
                Integer.parseInt(sss.split(" ")[0]), Integer.parseInt(ss.split(":")[0]), Integer.parseInt(ss.split(":")[1]));
    }

    public static Date StringToDate2(String text) {
        String sss = text.split("-")[2];
        String ss = sss.split(" ")[1];
        return new Date(Integer.parseInt(text.split("-")[0]) - 1900, Integer.parseInt(text.split("-")[1]) - 1,
                Integer.parseInt(sss.split(" ")[0]), Integer.parseInt(ss.split(":")[0]), Integer.parseInt(ss.split(":")[1]));
    }

    public static Date StringToDay(String text) {
        int year;
        if (Integer.parseInt(text.split("-")[0]) > 2000) year = Integer.parseInt(text.split("-")[0]);
        else year = 2000 + Integer.parseInt(text.split("-")[0]);
        return new Date(year - 1900, Integer.parseInt(text.split("-")[1]) - 1,
                Integer.parseInt(text.split("-")[2]));
    }

    public static Date convertHourNull(Date date) {
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        date = new Date(calendar.get(Calendar.YEAR) - 1900,
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        return date;
    }

    public static Date convertDayNull(Date date) {
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        date = new Date(calendar.get(Calendar.YEAR) - 1900,
                calendar.get(Calendar.MONTH), 1);
        return date;
    }

    public static List<String[]> buildNavigation(String[][] navigationRaw) {

        List<String[]> navigations = new ArrayList<String[]>();
        for (String[] nav : navigationRaw)
            navigations.add(new String[]{nav[0], nav[1]});

        return navigations;

    }

    public static String getSha1String(String pass) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] sha1hash = new byte[40];
            md.update(pass.getBytes("iso-8859-1"), 0, pass.length());
            sha1hash = md.digest();
            return convertToHex(sha1hash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }


    //Author: bb1987
    //Traverses and saves nodes of given dir
    private static void traverse(java.io.File dir, List<Object[]> dirs) {

        if (dir.isFile()) dirs.add(new Object[]{null, dir});

        else if (dir.isDirectory()) {
            List<Object[]> dirStruct = new ArrayList<Object[]>();
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                traverse(new java.io.File(dir, children[i]), dirStruct);
            }
            dirs.add(0, new Object[]{dir, dirStruct});
        }

    }

    private static final char[] PASSWORD = "sldkfjsldkfj434sdf_+-5678djfjf_-h".toCharArray();
    private static final byte[] SALT = {
            (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
            (byte) 0xdc, (byte) 0x83, (byte) 0x13, (byte) 0x02,
    };

    public static void main(String[] args) throws Exception {
        String originalPassword = "secret";
        System.out.println("Original password: " + originalPassword);
        String encryptedPassword = encrypt(originalPassword);
        System.out.println("Encrypted password: " + encryptedPassword);
        String decryptedPassword = decrypt(encryptedPassword);
        System.out.println("Decrypted password: " + decryptedPassword);
    }

    public static String encrypt(String property) {
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
            return base64Encode(pbeCipher.doFinal(property.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String base64Encode(byte[] bytes) {
        // NB: This class is internal, and you probably should use another impl
        return new String(Base64.encodeBase64URLSafe(bytes));
//        return new BASE64Encoder().encode(bytes);
    }

    public static String decrypt(String property) {
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
            return new String(pbeCipher.doFinal(base64Decode(property)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] base64Decode(String property) throws IOException {
        // NB: This class is internal, and you probably should use another impl
        return Base64.decodeBase64(property);
//        return new BASE64Decoder().decodeBuffer(property);
    }

    public static String encodeURIComponent(String s) {
        String result = null;

        try {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        }

        // This exception should never occur.
        catch (UnsupportedEncodingException e) {
            result = s;
        }

        return result;
    }


    public static Map<String, Object> getTemplateVariables(Object... args) {
        Map<String, Object> templateBinding = new HashMap<String, Object>();
        for (Object o : args) {
            List<String> names = LocalvariablesNamesEnhancer.LocalVariablesNamesTracer.getAllLocalVariableNames(o);
            for (String name : names) {
                templateBinding.put(name, o);
            }
        }
        return templateBinding;
    }

    public static String cleanUrl(String url) {
        return url.replace("//", "/").replace("///", "/");
    }

    public static String cleanUrlSlash(String url) {
        String val = "";
        for (int i = 0; i < url.length(); i++) {
            if ((int) url.charAt(i) == 92) val += "/";
            else val += url.charAt(i);
        }
        return val;
    }

    public static int showRandomInteger(int aStart, int aEnd, Random aRandom) {
        if (aStart > aEnd) {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        //get the range, casting to long to avoid overflow problems
        long range = (long) aEnd - (long) aStart + 1;
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long) (range * aRandom.nextDouble());
        return (int) (fraction + aStart);
    }

    public static String getDecimal(Long val) {
        int c = 1;
        String vv = "", value = "";
        if (val != null) value = val.toString();
        for (int i = value.length() - 1; i >= 0; i--) {
            if (i != 0 && c == 3) {
                vv += value.charAt(i) + "'";
                c = 1;
            } else {
                vv += value.charAt(i);
                c++;
            }
        }
        value = "";
        for (int i = vv.length() - 1; i >= 0; i--) {
            value += vv.charAt(i);
        }
        return value;
    }

    public static String getFloatFormat(float floatVar, int dec) {
        DecimalFormat format;
        if (dec == 1) format = new DecimalFormat("#.#");
        else if (dec == 2) format = new DecimalFormat("#.##");
        else format = new DecimalFormat("#.###");
        if (floatVar == Math.floor(floatVar)) {
            return Integer.toString((int) floatVar);
        } else
            return format.format(floatVar);
    }

    public static String getFloatFormatDec(Double floatVar) {
        NumberFormat nf1 = NumberFormat.getInstance();
        return nf1.format(floatVar);
    }

    public static String handleDocumentAttachment(String fileDir, String fileName, String extension) {

        String img = "<img src='#src' class='imgIcon' fileDir='#fileDir' type='#type' fileName='#fileName' extension='#extension'>";
        if (Consts.imageFileExtensions.contains(extension)) {
            img = img.replace("#src", fileDir + "t.jpg")
                    .replace("#type", Consts.imageFileType);
        } else if (Consts.wordFileExtensions.contains(extension)) {
            img = img.replace("#src", Consts.wordFileIcon);
        } else if (Consts.excelFileExtensions.contains(extension)) {
            img = img.replace("#src", Consts.excelFileIcon);
        } else if (Consts.powerPointFileExtensions.contains(extension)) {
            img = img.replace("#src", Consts.powerPointFileIcon);
        } else if (Consts.autoCadFileExtensions.contains(extension)) {
            img = img.replace("#src", Consts.autoCadFileIcon);
        } else if (Consts.pdfFileExtensions.contains(extension)) {
            img = img.replace("#src", Consts.pdfFileIcon);
        } else if (Consts.videoFileExtensions.contains(extension)) {
            img = img.replace("#src", Consts.videoFileIcon);
        } else if (Consts.viewableVideoExtensions.contains(extension)) {
            img = img.replace("#src", Consts.viewableVideoIcon).replace("#type", Consts.viewableVideoType);
        } else {
            img = img.replace("#src", Consts.otherFileIcon);
        }
        img = img.replace("#fileDir", fileDir).replace("#fileName", fileName).replace("#extension", extension);

        return img;
    }

    public static String handleDocumentIcon(String fileDir, String fileName, String extension) {

        String img = "<img src='#src' class='imgIcon' fileDir='#fileDir' type='#type'" +
                " fileName='#fileName' extension='#extension' style='height: 18px;vertical-align: bottom;margin-left:2px'>";
        if (Consts.imageFileExtensions.contains(extension)) {
            img = img.replace("#src", Consts.imageFileIcon);
        } else if (Consts.wordFileExtensions.contains(extension)) {
            img = img.replace("#src", Consts.wordFileIcon);
        } else if (Consts.excelFileExtensions.contains(extension)) {
            img = img.replace("#src", Consts.excelFileIcon);
        } else if (Consts.powerPointFileExtensions.contains(extension)) {
            img = img.replace("#src", Consts.powerPointFileIcon);
        } else if (Consts.autoCadFileExtensions.contains(extension)) {
            img = img.replace("#src", Consts.autoCadFileIcon);
        } else if (Consts.pdfFileExtensions.contains(extension)) {
            img = img.replace("#src", Consts.pdfFileIcon);
        } else if (Consts.videoFileExtensions.contains(extension)) {
            img = img.replace("#src", Consts.videoFileIcon);
        } else if (Consts.viewableVideoExtensions.contains(extension)) {
            img = img.replace("#src", Consts.viewableVideoIcon).replace("#type", Consts.viewableVideoType);
        } else {
            img = img.replace("#src", Consts.otherFileIcon);
        }
        img = img.replace("#fileDir", fileDir).replace("#fileName", fileName).replace("#extension", extension);

        return img;
    }

    public static String handleDocumentAttachmentDel(String fileDir, String fileName, String extension) {

        return "<img height=12 width=12 src='/public/images/delete-image.png' class='deleteUploadedImage' fileDir='" + fileDir + "' extension='" + extension + "'>";

    }

    public static String handleDocumentAttachmentNonDel(String fileDir, String fileName, String extension) {

        return "<img height=12 width=12 src='/public/images/delete-image.png' class='deleteImage' fileDir='" + fileDir + "' extension='" + extension + "'>";

    }

    public static int startFinishDateRange(Date startDate, Date finishDate, Date date) {
        if (startDate == null || finishDate == null) return 0;
        else if (startDate.compareTo(date) == 0) return 1;
        else if (startDate.getTime() <= date.getTime() && date.getTime() <= finishDate.getTime()) return 2;
        return 0;
    }

    public static MyWeekVal weekRange(Date startDate, Date finishDate, Date date, int week, int year) {
        if (startDate == null || finishDate == null) return new MyWeekVal(0, 0);
        Calendar cal = Functions.setCalDay(Calendar.getInstance(Locale.GERMAN));
        cal.setTime(startDate);
        Calendar cal2 = Functions.setCalDay(Calendar.getInstance(Locale.GERMAN));
        cal2.setTime(finishDate);

        if (cal.get(Calendar.WEEK_OF_MONTH) == week && cal.get(Calendar.YEAR) == year) {
            int w;
            if (cal2.get(Calendar.WEEK_OF_YEAR) < cal.get(Calendar.WEEK_OF_YEAR)) {
                int fw = cal2.get(Calendar.WEEK_OF_YEAR);
                if (fw == 1) {
                    if (cal2.get(Calendar.MONTH) == 11) fw = 0;
                }
                w = cal.getMaximum(Calendar.WEEK_OF_YEAR) - cal.get(Calendar.WEEK_OF_YEAR) + fw;
            } else w = cal2.get(Calendar.WEEK_OF_YEAR) - cal.get(Calendar.WEEK_OF_YEAR);
            return new MyWeekVal(w + 1, 1);
        } else if (startDate.getTime() <= date.getTime() && date.getTime() <= finishDate.getTime())
            return new MyWeekVal(0, 2);
        return new MyWeekVal(0, 0);
    }

    public static MyWeekVal monthRange(Date startDate, Date finishDate, Date date, int month, int year) {
        if (startDate == null || finishDate == null) return new MyWeekVal(0, 0);
        Calendar cal = Functions.setCalDay(Calendar.getInstance(Locale.GERMAN));
        cal.setTime(startDate);
        Calendar cal2 = Functions.setCalDay(Calendar.getInstance(Locale.GERMAN));
        cal2.setTime(finishDate);

        if (cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == year) {
            int w;
            if (cal2.get(Calendar.MONTH) < cal.get(Calendar.MONTH)) {
                w = cal.getMaximum(Calendar.MONTH) - cal.get(Calendar.MONTH) + cal2.get(Calendar.MONTH) + 1;
            } else w = cal2.get(Calendar.MONTH) - cal.get(Calendar.MONTH);
            return new MyWeekVal(w + 1, 1);
        } else if (startDate.getTime() <= date.getTime() && date.getTime() <= finishDate.getTime())
            return new MyWeekVal(0, 2);
        return new MyWeekVal(0, 0);
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

    public static void deleteFileSingle(String fileDir) {
        try {
            File file = new File(Play.applicationPath.getAbsolutePath() + fileDir);
            if (file.exists()) file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int randomNumber(int max) {
        Random generator = new Random();
        max--;
        int randomPic = generator.nextInt(max);
        randomPic++;
        return randomPic;
    }

    public static int getDifferenceDays(Date d1, Date d2) {
        int daysdiff = 0;
        long diff = d2.getTime() - d1.getTime();
        if (diff < 0) return daysdiff;
        long diffDays = diff / (24 * 60 * 60 * 1000) + 1;
        daysdiff = (int) diffDays;
        return daysdiff;
    }
}