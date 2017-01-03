package com.github.aarsy.loginviagithubapi.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by abhay yadav on 04-Dec-16.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UserProfiles";
    private static final String TABLE_PROFILE = "profile";
    public static final String KEY_USER_DATA = "userdata";
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "place_name";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_REPOS_DATA = "repos";
    Context ctx;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctx = context;
    }

    // Creating Tables  
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PROFILE_TABLE = "CREATE TABLE " + TABLE_PROFILE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_USERNAME + " TEXT,"
                + KEY_REPOS_DATA + " TEXT,"
                + KEY_USER_DATA + " TEXT,"
                + KEY_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";

        db.execSQL(CREATE_PROFILE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        switch (oldVersion) {
            case 1:
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
                // Create tables again
                onCreate(db);
                break;
        }

    }

    public void updateUserRepos(String username, String repos) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_REPOS_DATA, repos);
        db.update(TABLE_PROFILE, cv, KEY_USERNAME + "='" + username + "'", null);
    }

    public void addUserProfile(String username, String user_data) {
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        if (checkProfileIfExists(username)) {
            ContentValues cv = new ContentValues();
            cv.put(KEY_TIMESTAMP, dateFormat.format(date));
            db.update(TABLE_PROFILE, cv, KEY_USERNAME + "='" + username + "'", null);
        } else {
            ContentValues values = new ContentValues();
            values.put(KEY_USERNAME, username);
            values.put(KEY_USER_DATA, user_data);
            values.put(KEY_TIMESTAMP, dateFormat.format(date));
            db.insertWithOnConflict(TABLE_PROFILE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }


    public String getUserProfile(String username) {
        String selectQuery = "SELECT " + KEY_USER_DATA + " FROM " + TABLE_PROFILE + " where " + KEY_USERNAME + "='" + username + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(KEY_USER_DATA));
        }
        return null;
    }

    public String getUserAllRepos(String username) {
        String selectQuery = "SELECT " + KEY_REPOS_DATA + " FROM " + TABLE_PROFILE + " where " + KEY_USERNAME + "='" + username + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(KEY_REPOS_DATA));
        }
        return null;
    }

    public String getUserRepo(String username, String repoId) {
        String selectQuery = "SELECT " + KEY_REPOS_DATA + " FROM " + TABLE_PROFILE + " where " + KEY_USERNAME + "='" + username + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            String str = cursor.getString(cursor.getColumnIndex(KEY_REPOS_DATA));
            try {
                JSONArray jsonArray = new JSONArray(str);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("id").equalsIgnoreCase(repoId))
                        return jsonObject.toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean checkProfileIfExists(String user_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean value;
        value = DatabaseUtils.queryNumEntries(db, TABLE_PROFILE, KEY_USERNAME + "='" + user_name + "'") > 0;
        return value;
    }


    public List<String> getAllProfiles() {
        List<String> list = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + TABLE_PROFILE + " ORDER BY " + KEY_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                list.add((cursor.getString(cursor.getColumnIndex(KEY_USER_DATA))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}