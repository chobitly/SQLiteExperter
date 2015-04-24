package com.lu.sqliteexperter.exporter;

import android.content.Context;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.lu.sqliteexperter.R;

@EBean
public class DefaultExporter extends Exporter {
	@RootContext
	Context context;

	@Override
	public Context context() {
		return context;
	}

	@Override
	public void export() {
		Toast.makeText(context, R.string.under_development, Toast.LENGTH_SHORT)
				.show();
	}
}
