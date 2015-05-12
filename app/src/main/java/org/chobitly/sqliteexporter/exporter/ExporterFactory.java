package org.chobitly.sqliteexporter.exporter;

import android.content.Context;
import android.database.Cursor;

/**
 * A factory which can produce instances of {@link Exporter}, and can be
 * overridden for testing.
 */
public class ExporterFactory {

    private static ExporterFactory instance = new ExporterFactory();

    /**
     * Creates an instance of {@link Exporter}.
     *
     * @param context the context
     */
    public static Exporter get(Context context, int exportType, Cursor cursor,
                               String path, boolean exportToFile) {
        return instance.newForContext(context, exportType, cursor, path, exportToFile);
    }

    /**
     * Returns the factory instance.
     */
    public static ExporterFactory getInstance() {
        return instance;
    }

    /**
     * Overrides the factory instance for testing. Don't forget to set it back
     * to the original value after testing.
     *
     * @param factory the factory
     */
    public static void overrideInstance(ExporterFactory factory) {
        instance = factory;
    }

    /**
     * Creates an instance of {@link Exporter}. Allows subclasses to override
     * for testing.
     *
     * @param context the context
     */
    protected Exporter newForContext(Context context, int exportType,
                                     Cursor cursor, String path, boolean exportToFile) {
        switch (exportType) {
            case 0:
                return JSONExporter_.getInstance_(context).setup(cursor, path, exportToFile);
            case 1:
                return XMLExporter_.getInstance_(context).setup(cursor, path, exportToFile);
            case 2:
                return CSVExporter_.getInstance_(context).setup(cursor, path, exportToFile);
            case 3:
                return TEXTExporter_.getInstance_(context).setup(cursor, path, exportToFile);
            default:
                return DefaultExporter_.getInstance_(context).setup(cursor, path, exportToFile);
        }
    }
}
