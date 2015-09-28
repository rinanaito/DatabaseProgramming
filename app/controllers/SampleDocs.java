package controllers;

import models.SampleDoc;
import models.SampleDocAttach;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by enkhamgalan on 1/11/15.
 */
public class SampleDocs extends CRUD {
    public static void blank() {
        render();
    }

    public static void create(String editor1, String[] filename, String[] filedir, String[] extension) {
        SampleDoc sampleDoc = new SampleDoc();
        sampleDoc.createdDate = new Date();
        sampleDoc.documentText = editor1;
        sampleDoc.docAttaches = new ArrayList<SampleDocAttach>();
        sampleDoc.create();
        for (int i = 0; i < filename.length; i++) {
            SampleDocAttach attach = new SampleDocAttach();
            attach.name = filename[i];
            attach.extension = extension[i];
            attach.dir = filedir[i];
            attach.sampleDoc = sampleDoc;
            attach.create();
        }
        list();
    }

    public static void list() {
        List<SampleDoc> sampleDocs = SampleDoc.find("order by createdDate desc").fetch();
        render(sampleDocs);
    }

    public static void show(Long id) {
        SampleDoc sampleDoc = SampleDoc.findById(id);
        render(sampleDoc);
    }

    public static void save(Long id, String editor1, String[] filename, String[] filedir, String[] extension) {
        SampleDoc sampleDoc = SampleDoc.findById(id);
        sampleDoc.documentText = editor1;
        for (SampleDocAttach attach : sampleDoc.docAttaches) {
            attach._delete();
        }
        sampleDoc.docAttaches = new ArrayList<SampleDocAttach>();
        sampleDoc.save();
        for (int i = 0; i < filename.length; i++) {
            SampleDocAttach attach = new SampleDocAttach();
            attach.name = filename[i];
            attach.extension = extension[i];
            attach.dir = filedir[i];
            attach.sampleDoc = sampleDoc;
            attach.create();
        }
        show(id);
    }

    public static void sendToUserTeam() {
        render();
    }
}
