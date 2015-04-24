package org.chobitly.sqliteexperter.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.UiThread;

@EBean
public class ProgressDialogUtil {
	@RootContext
	Context context;

	ProgressDialog progressDialog;

	public void show(int titleResId, int max) {
		show(context.getText(titleResId), max, false);
	}

	public void show(int titleResId, int max, boolean cancelable) {
		show(context.getText(titleResId), max, cancelable);
	}

	public void show(CharSequence title, int max) {
		show(title, max, false);
	}

	@UiThread
	public void show(CharSequence title, int max, boolean cancelable) {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(cancelable);
		progressDialog.setTitle(title);
		progressDialog.setMax(max);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.show();
	}

	@UiThread
	public void update(int progress) {
		progressDialog.setProgress(progress);
	}

	public void dismiss(int resId) {
		dismiss(context.getText(resId));
	}

	@UiThread
	public void dismiss(CharSequence text) {
		progressDialog.dismiss();
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}
