package com.example.whatshistory.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.whatshistory.Models.CallsModel;
import com.example.whatshistory.Models.HistoryModel;
import com.example.whatshistory.Models.MessagesModel;

import java.util.ArrayList;

public class Sqlitedatabase extends SQLiteOpenHelper {
    private String tablename = "SendMessagesHistory";

    public Sqlitedatabase(Context context) {
        super(context, "Database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tableEmp = "create table " + tablename + " (Name text,Number text,Date text)";
        db.execSQL(tableEmp);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            String DROP_TABLE = "DROP TABLE IF EXISTS " + tablename;
            db.execSQL(DROP_TABLE);
            onCreate(db);
        } catch (Exception e) {
        }
    }

    public void insertData(MessagesModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Name", model.getName());
        values.put("Number", model.getNumber());
        values.put("Date", model.getDate());
        db.insertWithOnConflict(tablename, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void insertData(CallsModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Name", model.getName());
        values.put("Number", model.getNumber());
        values.put("Date", model.getDate());
        db.insertWithOnConflict(tablename, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public void insertData(HistoryModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Name", model.getName());
        values.put("Number", model.getNumber());
        values.put("Date", model.getDate());
        db.insertWithOnConflict(tablename, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }


    public ArrayList<HistoryModel> getAllHistory() {
        ArrayList<HistoryModel> contactList = new ArrayList<HistoryModel>();
        String selectQuery = "SELECT  * FROM " + tablename + " ORDER by Date DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                HistoryModel contact = new HistoryModel();
                contact.setName(cursor.getString(0));
                contact.setNumber(cursor.getString(1));
                contact.setDate(cursor.getString(2));
                contactList.add(contact);
            }
        } else {
            return new ArrayList<>();
        }
        return contactList;
    }
}