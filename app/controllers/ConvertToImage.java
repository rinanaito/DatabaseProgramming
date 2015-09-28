package controllers;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.Graphics2D;
import javax.imageio.ImageWriter;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import java.util.Iterator;
import javax.imageio.IIOImage;
import java.awt.Image;

/**
 * Created by IntelliJ IDEA.
 * User: enkhamgalan
 * Date: 12/15/14
 * Time: 10:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConvertToImage extends JPanel {

    public boolean convert(String path, String fileExtension, int w, int h, boolean ratio,boolean tumb) {
        Image image = null;
        int x, y, q = 1;
        try {
            try {
                path = path.substring(1, path.length());

                File file = new File(path + "." + fileExtension);
                image = ImageIO.read(file);
            } catch (Exception eo) {
                System.out.println(eo);
                return false;
            }
            int w2, h2, ww, hh;
            w2 = image.getWidth(this);
            h2 = image.getHeight(this);
            if (ratio) {
                float rw = w2 / w;
                float rh = h2 / h;
                 if (rh > rw) rh = rw;
                w = (int) (rh * w);
                h = (int) (rh * h);
            }
            BufferedImage bit = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D bigt = bit.createGraphics();
            bigt.setBackground(Color.white);
            bigt.clearRect(0, 0, w, h);

            hh = (h2 * w) / w2;
            ww = (w2 * h) / h2;
            if (hh <= h) {
                y = (h - hh) / 2;
                x = 0;
                h2 = hh;
                w2 = w;
            } else {
                x = (w - ww) / 2;
                y = 0;
                h2 = h;
                w2 = ww;
            }
            bigt.drawImage(image, x, y, w2, h2, this);
            bit.flush();
            Iterator iter = ImageIO.getImageWritersByFormatName("jpg");
            ImageWriter writer = (ImageWriter) iter.next();
            ImageWriteParam iwp = writer.getDefaultWriteParam();
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwp.setCompressionQuality(q);   // an integer between 0 and 1
            if(tumb)path+="t.jpg";
            else path+=".jpg";
            File selectedfile = new File(path);
            IIOImage imageio = new IIOImage(bit, null, null);
            FileImageOutputStream output = new FileImageOutputStream(selectedfile);
            writer.setOutput(output);
            writer.write(null, imageio, iwp);
            writer.dispose();
            output.close();
        } catch (Exception ef) {
            System.out.println("Save Image error:" + ef);
            return false;
        }
        return true;
    }
}
