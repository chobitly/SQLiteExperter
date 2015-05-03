package org.chobitly.sqliteexporter.pref;

import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(SharedPref.Scope.APPLICATION_DEFAULT)
public interface CachePref {

    /**
     * 最后一次选择的SQLite数据库路径
     */
    @DefaultString("")
    public String lastSQLiteFilePath();

    /**
     * 最后一次输入的SQL语句
     */
    @DefaultString("")
    public String lastSQL();

    /**
     * 最后一次选择的导出类型
     */
    @DefaultInt(0)
    public int lastExportFileType();

    /**
     * 最后一次选择的输出文件路径
     */
    @DefaultString("")
    public String lastExportFilePath();
}
