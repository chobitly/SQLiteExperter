package org.chobitly.sqliteexporter.exporter;

import android.content.Context;
import android.widget.Toast;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.chobitly.sqliteexporter.R;

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
