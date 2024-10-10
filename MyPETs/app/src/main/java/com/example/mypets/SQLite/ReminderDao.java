package com.example.mypets.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mypets.Model.Reminder;

import java.util.ArrayList;
import java.util.List;

public class ReminderDao {
    private final SQLiteHelper dbHelper;
    private SQLiteDatabase db;

    public ReminderDao(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void close() {
        dbHelper.close();

        if (db != null)
            db.close();
    }

    public List<Reminder> getAll(int petId, boolean hasDone) {
        List<Reminder> reminderForPet = new ArrayList<>();
        db = dbHelper.getReadableDatabase();

        String selection = "pet_id = ? AND has_done = ?";
        String[] selectionArgs = {String.valueOf(petId), hasDone ? "1" : "0"};
        String order = "date ASC, time ASC";

        Cursor cursor = db.query("Reminders", null, selection, selectionArgs, null, null, order);

        while (cursor != null && cursor.moveToNext()) {
            reminderForPet.add(new Reminder(cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5),
                    cursor.getString(6)
            ));
        }

        if (cursor != null)
            cursor.close();

        return reminderForPet;
    }

    public long add(Reminder reminder) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("pet_id", reminder.getPetId());
        values.put("type", reminder.getType());
        values.put("date", reminder.getDate());
        values.put("time", reminder.getTime());
        values.put("has_done", reminder.isHasDone());

        return db.insert("reminders", null, values);
    }

    public int update(Reminder reminder) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("pet_id", reminder.getPetId());
        values.put("type", reminder.getType());
        values.put("date", reminder.getDate());
        values.put("time", reminder.getTime());
        values.put("has_done", reminder.isHasDone());

        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(reminder.getId())};

        return db.update("reminders", values, whereClause, whereArgs);
    }


}
