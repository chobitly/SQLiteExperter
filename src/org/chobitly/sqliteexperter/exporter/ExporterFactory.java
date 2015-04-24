package org.chobitly.sqliteexperter.exporter;

import org.chobitly.sqliteexperter.exporter.CSVExporter_;
import org.chobitly.sqliteexperter.exporter.DefaultExporter_;
import org.chobitly.sqliteexperter.exporter.JSONExporter_;
import org.chobitly.sqliteexperter.exporter.TEXTExporter_;
import org.chobitly.sqliteexperter.exporter.XMLExporter_;

import android.content.Context;
import android.database.Cursor;

/**
 * A factory which can produce instances of {@link MyTracksProviderUtils}, and
 * can be overridden for testing.
 */
public class ExporterFactory {

	private static ExporterFactory instance = new ExporterFactory();

	/**
	 * Creates an instance of {@link MyTracksProviderUtils}.
	 * 
	 * @param context
	 *            the context
	 */
	public static Exporter get(Context context, int exportType, Cursor cursor,
			String path) {
		return instance.newForContext(context, exportType, cursor, path);
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
	 * @param factory
	 *            the factory
	 */
	public static void overrideInstance(ExporterFactory factory) {
		instance = factory;
	}

	/**
	 * Creates an instance of {@link Exporter}. Allows subclasses to override
	 * for testing.
	 * 
	 * @param context
	 *            the context
	 */
	protected Exporter newForContext(Context context, int exportType,
			Cursor cursor, String path) {
		switch (exportType) {
		case 0:
			return JSONExporter_.getInstance_(context).setup(cursor, path);
		case 1:
			return XMLExporter_.getInstance_(context).setup(cursor, path);
		case 2:
			return CSVExporter_.getInstance_(context).setup(cursor, path);
		case 3:
			return TEXTExporter_.getInstance_(context).setup(cursor, path);
		default:
			return DefaultExporter_.getInstance_(context).setup(cursor, path);
		}
	}
}
