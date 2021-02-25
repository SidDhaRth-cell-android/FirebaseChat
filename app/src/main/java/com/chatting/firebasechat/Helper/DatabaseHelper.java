package com.chatting.firebasechat.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "messenger.db";
    public static String TABLE_NAME = "membersId";
    public static String COLUMN_GROUP_ID = "groupId";
    public static String COLUMN_MEMBERS = "membersId";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = " CREATE TABLE " + TABLE_NAME + "(groupId TEXT,membersId TEXT)";
        sqLiteDatabase.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addMembersId(String groupId, String membersId) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_ID, groupId);
        values.put(COLUMN_MEMBERS, membersId);
        long res = database.insert(TABLE_NAME, null, values);
        if (res > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteMembers(String groupId, String membersId) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_NAME, "groupId=" + groupId + " AND membersId=" + membersId, null);
        return true;
    }

    public List<String> getTableData(String groupId) {
        List<String> membersId = new ArrayList<>();
        SQLiteDatabase database = this.getWritableDatabase();
        String sql = " SELECT membersId FROM " + TABLE_NAME + " WHERE groupId=" + groupId;
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            membersId.add(cursor.getString(cursor.getColumnIndex(COLUMN_MEMBERS)));
        }
        return membersId;
    }


}
