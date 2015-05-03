package org.chobitly.sqliteexporter.exporter;

import android.content.Context;
import android.database.Cursor;

import org.chobitly.sqliteexporter.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public abstract class Exporter {
    protected Cursor cursor = null;
    protected BufferedWriter out = null;

    public abstract Context context();

    /**
     * must call before export().
     *
     * @param cursor
     * @param path
     */
    public Exporter setup(Cursor cursor, String path) {
        if (cursor == null) {
            throw new IllegalArgumentException(context().getText(
                    R.string.make_sure_sql_right).toString());
        }
        this.cursor = cursor;

        try {
            File file = new File(path);
            if (file.exists()) {
                File bakFile = new File(file.getAbsolutePath() + ".bak");
                bakFile.delete();// 删去旧备份
                file.renameTo(bakFile);// 旧文件改名备份
            }
            file.createNewFile();
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(context().getText(
                    R.string.make_sure_export_file_right).toString());
        }
        return this;
    }

    public abstract void export();

}
