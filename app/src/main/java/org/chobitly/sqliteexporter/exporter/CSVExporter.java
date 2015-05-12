package org.chobitly.sqliteexporter.exporter;

import android.database.Cursor;

import org.androidannotations.annotations.EBean;

@EBean
public class CSVExporter extends TEXTExporter {

    public Exporter setup(Cursor cursor, String path, boolean exportToFile) {
        this.separator = ",";
        return super.setup(cursor, path, exportToFile);
    }
}
