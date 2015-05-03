package org.chobitly.sqliteexporter.exporter;

import android.content.Context;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.chobitly.sqliteexporter.R;
import org.chobitly.sqliteexporter.util.ProgressDialogUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

@EBean
public class JSONExporter extends Exporter {
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
            out.write("[\n");
            cursor.moveToPosition(-1);// 确认移动到第一条记录之前
            while (cursor.moveToNext()) {
                if (cursor.getPosition() > 0) {
                    out.write(",\n");
                }
                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < cursor.getColumnCount(); ++i) {
                    try {
                        jsonObject.put(cursor.getColumnName(i),
                                cursor.getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    out.write(jsonObject.toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialogUtil.update(cursor.getPosition() + 1);
            }
            out.write("\n]");
            out.flush();
            out.close();
            progressDialogUtil.dismiss(R.string.export_success);
        } catch (IOException e1) {
            e1.printStackTrace();
            progressDialogUtil.dismiss(R.string.export_failed);
        }
    }
}
