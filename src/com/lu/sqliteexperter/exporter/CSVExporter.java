package com.lu.sqliteexperter.exporter;

import java.io.IOException;

import android.content.Context;
import android.text.TextUtils;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.lu.sqliteexperter.R;
import com.lu.sqliteexperter.util.ProgressDialogUtil;

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
