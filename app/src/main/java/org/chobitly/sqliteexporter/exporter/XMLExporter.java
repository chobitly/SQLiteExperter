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
public class XMLExporter extends Exporter {
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
            append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            append("<root>\n");
            cursor.moveToPosition(-1);// 确认移动到第一条记录之前
            while (cursor.moveToNext()) {
                append("\t<record>\n");
                for (int i = 0; i < cursor.getColumnCount(); ++i) {
                    if (TextUtils.isEmpty(cursor.getString(i))) {
                        append("\t\t<" + cursor.getColumnName(i) + "/>\n");
                    } else {
                        append("\t\t<" + cursor.getColumnName(i) + ">");
                        append(cursor.getString(i));
                        append("</" + cursor.getColumnName(i) + ">\n");
                    }
                }
                append("\t</record>\n");
                progressDialogUtil.update(cursor.getPosition() + 1);
            }
            append("</root>");
            onExportFinish();
            progressDialogUtil.dismiss(R.string.export_success);
        } catch (IOException e) {
            e.printStackTrace();
            progressDialogUtil.dismiss(R.string.export_failed);
        }
    }
}
