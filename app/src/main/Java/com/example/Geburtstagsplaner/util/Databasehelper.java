package com.example.Geburtstagsplaner.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.Geburtstagsplaner.pojo.Birthday;

import java.util.ArrayList;

public class Databasehelper extends SQLiteOpenHelper {

    private static final String COLUMN_0 = "id";
    private static final String COLUMN_1 = "name";
    private static final String COLUMN_2 = "year";
    private static final String COLUMN_3 = "month";
    private static final String COLUMN_4 = "day";
    private static final String TABLE_NAME = "birthdays";

    public Databasehelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_0 + " INTEGER PRIMARY KEY,"
                + COLUMN_1 + " TEXT," + COLUMN_2 + " INTEGER," + COLUMN_3 + " INTEGER, "
                + COLUMN_4 + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<Birthday> getBirthdaysByMonth(int month) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Birthday> listOfBirthdays = new ArrayList<>();
        Cursor data = db.rawQuery("SELECT " + " * " + " FROM " + TABLE_NAME +
                " WHERE " + COLUMN_3 + "=" + month, null);
        while (data.moveToNext()) {
            listOfBirthdays.add(new Birthday(data.getString(1), data.getInt(2)
                    , month, data.getInt(4)));
        }
        data.close();
        db.close();
        return listOfBirthdays;
    }

    public boolean addBirthday(Birthday birthday) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues(4);
        contentValues.put(COLUMN_1, birthday.getName());
        contentValues.put(COLUMN_2, birthday.getYear());
        contentValues.put(COLUMN_3, birthday.getMonth());
        contentValues.put(COLUMN_4, birthday.getDay());
        long result = db.insert(TABLE_NAME, null, contentValues);

        db.close();
        return result != -1;
    }

    public ArrayList<Birthday> getAllBirthdays() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        ArrayList<Birthday> listOfBirthdays = new ArrayList<>();
        while (data.moveToNext()) {
            Birthday birthday = new Birthday();
            birthday.setName(data.getString(1));
            birthday.setDate(data.getInt(2), data.getInt(3),
                    data.getInt(4));
            listOfBirthdays.add(birthday);
        }
        data.close();
        db.close();
        return listOfBirthdays;
    }

    public boolean updateBirthdayById(Birthday birthday, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues(4);
        contentValues.put(COLUMN_1, birthday.getName());
        contentValues.put(COLUMN_2, birthday.getYear());
        contentValues.put(COLUMN_3, birthday.getMonth());
        contentValues.put(COLUMN_4, birthday.getDay());
        long result = db.update(TABLE_NAME, contentValues, COLUMN_0 + "=" +
                id, null);
        db.close();
        return result == 1;
    }

    public boolean deleteBirthdayById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean result = db.delete(TABLE_NAME, COLUMN_0 + "=" + id, null) == 1;
        db.close();
        return result;
    }

    public int getIdByBirthday(Birthday birthday) {
        int id = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT " + COLUMN_0 + " FROM " + TABLE_NAME + " WHERE " + COLUMN_1
                        + " = '" + birthday.getName() + "' AND " + COLUMN_2 + " = " + birthday.getYear() + " " +
                        "AND " + COLUMN_3 + " = " + birthday.getMonth() + " AND " + COLUMN_4 + " = " + birthday.getDay(),
                null);
        while (data.moveToNext()) {
            id = data.getInt(0);
        }
        data.close();
        db.close();
        return id;
    }
}
