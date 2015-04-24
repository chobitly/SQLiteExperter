package org.chobitly.sqliteexperter;

import java.util.ArrayList;
import java.util.HashMap;

import org.chobitly.sqliteexperter.R;
import org.chobitly.sqliteexperter.exporter.ExporterFactory;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterTextChange;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OnActivityResult;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.StringArrayRes;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;
import org.chobitly.sqliteexperter.pref.CachePref_;

@EActivity(R.layout.activity_main)
// @OptionsMenu(R.menu.main)
public class MainActivity extends Activity {
	private static final int SQLITE_FILE_SELECT_CODE = 0x000000f1;
	private static final int EXPORT_FILE_SELECT_CODE = 0x000000f2;

	@StringArrayRes(R.array.export_file_types)
	String[] TYPES;

	@Pref
	CachePref_ mCachePref;

	@ViewById(R.id.edittext_sqlite_file_path)
	EditText mSQLiteFilePathView;
	@ViewById(R.id.edittext_sql)
	EditText mSQLView;
	@ViewById(R.id.spinner_export_file_type)
	Spinner mExportFileTypeView;
	@ViewById(R.id.edittext_export_file_path)
	EditText mExportFilePathView;

	@AfterViews
	protected void setup() {
		// load last selections
		mSQLiteFilePathView.setText(mCachePref.lastSQLiteFilePath().get());
		mSQLView.setText(mCachePref.lastSQL().get());
		mExportFilePathView.setText(mCachePref.lastExportFilePath().get());

		// setup spinner
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < TYPES.length; ++i) {
			HashMap<String, Object> map = null;
			map = new HashMap<String, Object>();
			map.put("type_name", TYPES[i]);
			list.add(map);
		}
		mExportFileTypeView
				.setAdapter(new SimpleAdapter(this, list,
						android.R.layout.simple_list_item_1,
						new String[] { "type_name" },
						new int[] { android.R.id.text1 }));
		mExportFileTypeView.setSelection(mCachePref.lastExportFileType().get());
	}

	@AfterTextChange(R.id.edittext_sqlite_file_path)
	protected void afterSQLiteFilePathChanged(Editable text) {
		mCachePref.edit().lastSQLiteFilePath().put(text.toString()).apply();
	}

	@Click(R.id.button_choose_sqlite_file_path)
	protected void chooseSQLiteFilePath() {
		showFileChooser(getText(R.string.specific_sqlite_file_path),
				SQLITE_FILE_SELECT_CODE);
	}

	@OnActivityResult(SQLITE_FILE_SELECT_CODE)
	protected void getSQLiteFilePath(Intent data) {
		if (data == null)
			return;
		Uri uri = data.getData();
		if (uri == null)
			return;

		Log.i("File Path", "uri:" + uri);
		if (uri.getPath().startsWith(
				Environment.getExternalStorageDirectory().getAbsolutePath())) {
			mSQLiteFilePathView.setText(uri.getPath());
		} else {
			mExportFilePathView.setText("");
		}
	}

	@AfterTextChange(R.id.edittext_sql)
	protected void afterSQLChanged(Editable text) {
		mCachePref.edit().lastSQL().put(text.toString()).apply();
	}

	@AfterTextChange(R.id.edittext_export_file_path)
	protected void afterExportFilePathChanged(Editable text) {
		mCachePref.edit().lastExportFilePath().put(text.toString()).apply();
	}

	@Click(R.id.button_choose_export_file_path)
	protected void chooseExportFilePath() {
		showFileChooser(getText(R.string.specific_export_file_path),
				EXPORT_FILE_SELECT_CODE);
	}

	@OnActivityResult(EXPORT_FILE_SELECT_CODE)
	protected void getExportFilePath(Intent data) {
		if (data == null)
			return;
		Uri uri = data.getData();
		if (uri == null)
			return;

		Log.i("File Path", "uri:" + uri);
		if (uri.getPath().startsWith(
				Environment.getExternalStorageDirectory().getAbsolutePath())) {
			mExportFilePathView.setText(uri.getPath());
		} else {
			mExportFilePathView.setText("");
		}
	}

	@Click(R.id.button_export)
	protected void export() {
		mCachePref.edit().lastExportFileType()
				.put(mExportFileTypeView.getSelectedItemPosition()).apply();
		String sql = checkSQL();
		if (!TextUtils.isEmpty(sql)) {
			try {
				Cursor cursor = SQLiteDatabase.openDatabase(
						mSQLiteFilePathView.getText().toString(), null,
						SQLiteDatabase.OPEN_READWRITE).rawQuery(sql, null);
				ExporterFactory.get(this,
						mExportFileTypeView.getSelectedItemPosition(), cursor,
						mExportFilePathView.getText().toString()).export();
			} catch (SQLiteCantOpenDatabaseException e) {
				Toast.makeText(this, R.string.make_sure_database_right,
						Toast.LENGTH_SHORT).show();
			} catch (IllegalArgumentException e) {
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				Toast.makeText(this, R.string.make_sure_sql_right,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * @return 如果所有输出都没有问题，返回可用的SQL语句，否则返回null。
	 */
	private String checkSQL() {
		if (TextUtils.isEmpty(mSQLiteFilePathView.getText().toString())) {
			Toast.makeText(this, R.string.make_sure_database_right,
					Toast.LENGTH_SHORT).show();
			return null;
		}

		String sql = mSQLView.getText().toString();
		if (TextUtils.isEmpty(sql)) {
			Toast.makeText(this, R.string.make_sure_sql_right,
					Toast.LENGTH_SHORT).show();
			return null;
		}
		if (sql.contains(";")) {
			if (sql.endsWith(";")) {
				sql.substring(0, sql.length() - 2);// 去除末尾的";"
			}
			String[] sqls = sql.split(";");
			if (sqls.length > 1) {
				Toast.makeText(this, R.string.only_one_sql_supported,
						Toast.LENGTH_SHORT).show();
				return null;
			}
		}

		if (TextUtils.isEmpty(mExportFilePathView.getText().toString())) {
			Toast.makeText(this, R.string.make_sure_export_file_right,
					Toast.LENGTH_SHORT).show();
			return null;
		}

		return sql;
	}

	private void showFileChooser(CharSequence title, int requestCode) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			startActivityForResult(Intent.createChooser(intent, title),
					requestCode);
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(this, R.string.please_install_file_manager,
					Toast.LENGTH_SHORT).show();
		}
	}
}
