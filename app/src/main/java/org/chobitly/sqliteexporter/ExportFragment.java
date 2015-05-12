package org.chobitly.sqliteexporter;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.chobitly.sqliteexporter.exporter.ExporterFactory;
import org.chobitly.sqliteexporter.pref.CachePref_;
import org.chobitly.sqliteexporter.util.FileChooserUtils;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_export)
public class ExportFragment extends Fragment {
    private static final int SQLITE_FILE_SELECT_CODE = 0x000000f1;
    private static final int EXPORT_FILE_SELECT_CODE = 0x000000f2;
    private static final long INVALID_ITEM_ID = -1;

    private long mItemID = INVALID_ITEM_ID;

    @StringArrayRes(R.array.export_file_types)
    String[] TYPES;

    @Pref
    CachePref_ mCachePref;

    @ViewById(R.id.fab)
    View mFAB;
    @ViewById(R.id.edittext_export_database)
    EditText mSQLiteFilePathView;
    @ViewById(R.id.edittext_export_sql)
    EditText mSQLView;
    @ViewById(R.id.spinner_export_file_type)
    Spinner mExportFileTypeView;
    @ViewById(R.id.switch_export_to_file)
    Switch mExportToFileView;
    @ViewById(R.id.edittext_export_file)
    EditText mExportFilePathView;

    public void setItemID(long itemID) {
        this.mItemID = itemID;
        // TODO 更新界面内容 loadControlsSelections
    }

    /**
     * load last selections
     *
     * @param lastDatabaseFilePath
     * @param lastSQL
     * @param lastFileTypeSelection
     * @param lastExportFilePath
     */
    private void loadControlsSelections(String lastDatabaseFilePath, String lastSQL,
                                        int lastFileTypeSelection, String lastExportFilePath) {
        mSQLiteFilePathView.setText(lastDatabaseFilePath == null ? "" : lastDatabaseFilePath);
        mSQLView.setText(lastSQL == null ? "" : lastSQL);
        mExportFileTypeView.setSelection(
                (lastFileTypeSelection < 0 || lastFileTypeSelection >= mExportFileTypeView.getCount())
                        ? 0 : lastFileTypeSelection);
        mExportToFileView.setChecked(mCachePref.lastExportToFile().get());
        mExportFilePathView.setEnabled(mCachePref.lastExportToFile().get());
        mExportFilePathView.setText(lastExportFilePath == null ? "" : lastExportFilePath);
    }

    @AfterViews
    protected void setupSpinner() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < TYPES.length; ++i) {
            HashMap<String, Object> map = null;
            map = new HashMap<>();
            map.put("type_name", TYPES[i]);
            list.add(map);
        }
        mExportFileTypeView
                .setAdapter(new SimpleAdapter(getActivity(), list,
                        android.R.layout.simple_list_item_1,
                        new String[]{"type_name"},
                        new int[]{android.R.id.text1}));
        loadControlsSelections(mCachePref.lastSQLiteFilePath().get(), mCachePref.lastSQL().get(),
                mCachePref.lastExportFileType().get(), mCachePref.lastExportFilePath().get());
    }


    @AfterViews
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setupFAB() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mFAB.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    // Or read size directly from the view's width/height
                    int size = getResources().getDimensionPixelSize(R.dimen.activity_fab_size);
                    outline.setOval(0, 0, size, size);
                }
            });
            mFAB.setClipToOutline(true);
            mFAB.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getActionMasked();
                /* Raise view on ACTION_DOWN and lower it on ACTION_UP. */
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            mFAB.setTranslationZ(getResources().getDimensionPixelSize(R.dimen.activity_fab_translation_z));
                            break;
                        case MotionEvent.ACTION_UP:
                            mFAB.setTranslationZ(0);
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }
    }

    @Click(R.id.folder_open_database)
    protected void chooseDatabasePath() {
        FileChooserUtils.showFileChooser(this, "file/*", getText(R.string.export_database), SQLITE_FILE_SELECT_CODE);
    }

    @Click(R.id.folder_open_export_file)
    protected void chooseExportFilePath() {
        FileChooserUtils.showFileChooser(this, "file/*", getText(R.string.export_file), EXPORT_FILE_SELECT_CODE);
    }

    @CheckedChange(R.id.switch_export_to_file)
    protected void onExportToFileChanged(boolean checked) {
        mExportFilePathView.setEnabled(checked);


    }

    /**
     * 导出
     */
    @Click(R.id.fab)
    public void export() {
        String sql = checkSQL();
        if (!TextUtils.isEmpty(sql)) {
            mCachePref.edit().lastSQL().put(sql)
                    .lastSQLiteFilePath().put(mSQLiteFilePathView.getText().toString())
                    .lastExportFileType().put(mExportFileTypeView.getSelectedItemPosition())
                    .lastExportToFile().put(mExportToFileView.isChecked())
                    .lastExportFilePath().put(mExportFilePathView.getText().toString())
                    .apply();
            try {
                Cursor cursor = SQLiteDatabase.openDatabase(
                        mSQLiteFilePathView.getText().toString(), null,
                        SQLiteDatabase.OPEN_READWRITE).rawQuery(sql, null);
                ExporterFactory.get(getActivity(),
                        mExportFileTypeView.getSelectedItemPosition(), cursor,
                        mExportFilePathView.getText().toString(), mExportToFileView.isChecked())
                        .export();
            } catch (SQLiteCantOpenDatabaseException e) {
                Toast.makeText(getActivity(), R.string.make_sure_database_right,
                        Toast.LENGTH_SHORT).show();
            } catch (IllegalArgumentException e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getActivity(), R.string.make_sure_sql_right,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * @return 如果所有输出设置都没有问题，返回可用的SQL语句，否则返回null。
     */
    private String checkSQL() {
        // 检查数据库文件路径
        if (TextUtils.isEmpty(mSQLiteFilePathView.getText().toString())) {
            mSQLView.setError(getText(R.string.make_sure_database_right));
            mSQLView.requestFocus();
            return null;
        }
        // 检查SQL语句
        String sql = mSQLView.getText().toString();
        if (TextUtils.isEmpty(sql)) {
            mSQLView.setError(getText(R.string.make_sure_sql_right));
            mSQLView.requestFocus();
            return null;
        }
        if (sql.contains(";")) {
            if (sql.endsWith(";")) {
                sql.substring(0, sql.length() - 2);// 去除末尾的";"
            }
            String[] sqls = sql.split(";");
            if (sqls.length > 1) {
                mSQLView.setError(getText(R.string.only_one_sql_supported));
                mSQLView.requestFocus();
                return null;
            }
        }
        // 检查输出文件路径
        if (TextUtils.isEmpty(mExportFilePathView.getText().toString())) {
            mSQLView.setError(getText(R.string.make_sure_export_file_right));
            mSQLView.requestFocus();
            return null;
        }
        return sql;
    }

    @OnActivityResult(SQLITE_FILE_SELECT_CODE)
    protected void getSQLiteFilePath(Intent data) {
        if (data == null)
            return;
        Uri uri = data.getData();
        if (uri == null)
            return;

        Log.i("File Path", "uri:" + uri);
        String path = FileChooserUtils.getPath(getActivity(), uri);
        mSQLiteFilePathView.setText(path == null ? "" : path);
    }

    @OnActivityResult(EXPORT_FILE_SELECT_CODE)
    protected void getExportFilePath(Intent data) {
        if (data == null)
            return;
        Uri uri = data.getData();
        if (uri == null)
            return;

        Log.i("File Path", "uri:" + uri);
        String path = FileChooserUtils.getPath(getActivity(), uri);
        mExportFilePathView.setText(path == null ? "" : path);
    }
}
