package org.chobitly.sqliteexperter.exporter;

import org.chobitly.sqliteexperter.R;

import android.content.Context;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;

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
