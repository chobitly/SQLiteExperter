package org.chobitly.sqliteexporter.exporter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.chobitly.sqliteexporter.R;
import org.chobitly.sqliteexporter.util.ProgressDialogUtil;

import java.io.IOException;

@EBean
public class TEXTExporter extends Exporter {
    @RootContext
    Context context;

    @Bean
    ProgressDialogUtil progressDialogUtil;

    String separator = "|";

    @Override
    public Context context() {
        return context;
    }

    public Exporter setup(Cursor cursor, String path, boolean exportToFile, String separator) {
        this.separator = separator;
        return super.setup(cursor, path, exportToFile);
    }

    @Override
    @Background
    public void export() {
        progressDialogUtil.show(R.string.exporting, cursor.getCount());
        try {
            append(TextUtils.join(separator, cursor.getColumnNames()));
            append("\n");
            cursor.moveToPosition(-1);// 确认移动到第一条记录之前
            while (cursor.moveToNext()) {
                for (int i = 0; i < cursor.getColumnCount(); ++i) {
                    if (!TextUtils.isEmpty(cursor.getString(i))) {
                        append(cursor.getString(i));
                    }
                    append(i == cursor.getColumnCount() - 1 ? "\n"
                            : separator);
                }
                progressDialogUtil.update(cursor.getPosition() + 1);
            }
            onExportFinish();
            progressDialogUtil.dismiss(R.string.export_success);
        } catch (IOException e1) {
            e1.printStackTrace();
            progressDialogUtil.dismiss(R.string.export_failed);
        }
    }
}
