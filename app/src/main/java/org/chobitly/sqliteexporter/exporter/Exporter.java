package org.chobitly.sqliteexporter.exporter;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;

import org.chobitly.sqliteexporter.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public abstract class Exporter {
    private static Handler _handler = new Handler();

    protected Cursor cursor = null;
    private boolean exportToFile = true;
    private BufferedWriter out = null;
    private StringBuffer exportStringBuffer = null;

    public abstract Context context();

    /**
     * must call before export().
     *
     * @param cursor
     * @param path
     */
    public Exporter setup(Cursor cursor, String path, boolean exportToFile) {
        if (cursor == null) {
            throw new IllegalArgumentException(context().getText(
                    R.string.make_sure_sql_right).toString());
        }
        this.cursor = cursor;
        this.exportToFile = exportToFile;
        if (exportToFile) {
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
        } else {
            exportStringBuffer = new StringBuffer();
        }

        return this;
    }

    public abstract void export();

    public void append(String string) throws IOException {
        if (exportToFile) {
            out.write(string);
        } else {
            exportStringBuffer.append(string);
        }
    }

    protected void onExportFinish() throws IOException {
        if (exportToFile) {
            out.flush();
            out.close();
        } else {
            if (Thread.currentThread() == Looper.getMainLooper().getThread()) {// 判断是否已经在主线程了，线程重用
                showExportDialog(exportStringBuffer.toString());
            } else {
                _handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showExportDialog(exportStringBuffer.toString());
                    }
                });
            }
        }
    }

    /**
     * should be called on UiThread.
     */
    private void showExportDialog(final String exportString) {
        new AlertDialog.Builder(context()).setTitle(R.string.export_success)
                //.setIcon(R.drawable.ic_export)
                .setMessage(exportString)
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.copy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager cmb = (ClipboardManager) context().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText(context().getText(R.string.app_name), exportString);
                        cmb.setPrimaryClip(clipData);
                    }
                }).show();
    }
}
