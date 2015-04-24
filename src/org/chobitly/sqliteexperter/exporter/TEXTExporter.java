package org.chobitly.sqliteexperter.exporter;

import java.io.IOException;

import org.chobitly.sqliteexperter.R;
import org.chobitly.sqliteexperter.util.ProgressDialogUtil;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;

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

	public Exporter setup(Cursor cursor, String path, String separator) {
		this.separator = separator;
		return super.setup(cursor, path);
	}

	@Override
	@Background
	public void export() {
		progressDialogUtil.show(R.string.exporting, cursor.getCount());
		try {
			for (int i = 0; i < cursor.getColumnCount(); ++i) {
				out.write(cursor.getColumnName(i));
				out.write(i == cursor.getColumnCount() - 1 ? "\n" : separator);
			}
			cursor.moveToPosition(-1);// 确认移动到第一条记录之前
			while (cursor.moveToNext()) {
				for (int i = 0; i < cursor.getColumnCount(); ++i) {
					if (!TextUtils.isEmpty(cursor.getString(i))) {
						out.write(cursor.getString(i));
					}
					out.write(i == cursor.getColumnCount() - 1 ? "\n"
							: separator);
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
