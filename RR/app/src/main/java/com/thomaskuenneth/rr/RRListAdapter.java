package com.thomaskuenneth.rr;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.io.File;
import java.io.FilenameFilter;

public class RRListAdapter extends ArrayAdapter<File> {

    public RRListAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        // vorhandene Dateien suchen und hinzufügen
        findAndAddFiles();
    }

    private void findAndAddFiles() {
        File dir = RR.getBaseDir();
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (!filename.toLowerCase().endsWith(RRFile.EXT_3GP)) {
                    return false;
                }
                File f = new File(dir, filename);
                return f.canRead() && !f.isDirectory();
            }
        });
        if (files != null) {
            for (File f : files) {
                add(new RRFile(f.getParentFile(), f.getName()));
            }
        }
    }
}
