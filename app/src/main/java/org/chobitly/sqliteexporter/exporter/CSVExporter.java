package org.chobitly.sqliteexporter.exporter;

import android.content.Context;
import android.text.TextUtils;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.chobitly.sqliteexporter.R;
import org.chobitly.sqliteexporter.util.ProgressDialogUtil;

import java.io.IOException;

@EBean
public class CSVExporter extends Exporter {
    @RootContext
    Context context;

    @Bean
    ProgressDialogUtil progressDialogUtil;

    @Override
    public Context context() {
        return context;
    }

    @Override
    @Background
    public void export() {
        progressDialogUtil.show(R.string.exporting, cursor.getCount());
        try {
            for (int i = 0; i < cursor.getColumnCount(); ++i) {
                out.write(cursor.getColumnName(i));
                out.write(i == cursor.getColumnCount() - 1 ? "\n" : ",");
            }
            cursor.moveToPosition(-1);// 确认移动到第一条记录之前
            while (cursor.moveToNext()) {
                for (int i = 0; i < cursor.getColumnCount(); ++i) {
                    if (!TextUtils.isEmpty(cursor.getString(i))) {
                        out.write(cursor.getString(i));
                    }
                    out.write(i == cursor.getColumnCount() - 1 ? "\n" : ",");
                }
                progressDialogUtil.update(cursor.getPosition() + 1);
            }
            out.flush();
            out.close();
            progressDialogUtil.dismiss(R.string.export_success);
        } catch (IOException e1) {
            e1.printStackTrace();
            progressDialogUtil.dismiss(R.string.export_failed);
        }
    }
}
