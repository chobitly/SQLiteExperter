package com.lu.sqliteexperter.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.UiThread;

@EBean
public class LoadingDialogUtil {
	@RootContext
	Context context;

	ProgressDialog progressDialog;

	public void show(int titleResId, int msgResId) {
		show(context.getText(titleResId), context.getText(msgResId), false);
	}

	public void show(int titleResId, int msgResId, boolean cancelable) {
		show(context.getText(titleResId), context.getText(msgResId),
				cancelable);
	}

	public void show(CharSequence title, CharSequence msg) {
		show(title, msg, false);
	}

	@UiThread
	public void show(CharSequence title, CharSequence msg, boolean cancelable) {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(cancelable);
		progressDialog.setTitle(title);
		progressDialog.setMessage(msg);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.show();
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
