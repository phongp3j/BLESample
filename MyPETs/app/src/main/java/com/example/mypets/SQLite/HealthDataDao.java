package com.example.mypets.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mypets.Model.HealthData;
import com.example.mypets.Model.Pet;

import java.util.ArrayList;
import java.util.List;

public class HealthDataDao {

    private final SQLiteHelper dbHelper;
    private SQLiteDatabase db;

    public HealthDataDao(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void close() {
        dbHelper.close();

        if (db != null)
            db.close();
    }

    public long add(HealthData healthData) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("pet_id", healthData.getPetId());
        values.put("heart_rate", healthData.getHeartRate());
        values.put("temperature", healthData.getTemperature());
        return db.insert("HealthData", null, values);
    }

    public List<HealthData> getAllByPetId(int petId) throws NullPointerException {
        List<HealthData> list = new ArrayList<>();
        db = dbHelper.getReadableDatabase();

        String selection = "pet_id = ?";
        String[] selectionArgs = {String.valueOf(petId)};
        String order = "recorded_at DESC";

        Cursor cursor = db.query("HealthData", null, selection, selectionArgs, null, null, order);

        while (cursor != null && cursor.moveToNext()) {
            list.add(new HealthData(cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getFloat(3),
                    cursor.getString(4))
            );
        }

        if (cursor != null)
            cursor.close();

        return list;
    }
}
