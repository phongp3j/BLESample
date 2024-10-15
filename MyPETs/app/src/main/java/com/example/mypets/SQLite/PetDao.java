package com.example.mypets.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mypets.Model.Pet;

import java.util.ArrayList;
import java.util.List;

public class PetDao {

    private final SQLiteHelper dbHelper;
    private SQLiteDatabase db;

    public PetDao(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void close() {
        dbHelper.close();

        if (db != null)
            db.close();
    }

    public List<Pet> getAll(int userId) {
        List<Pet> list = new ArrayList<>();
        db = dbHelper.getReadableDatabase();

        String selection = "user_id = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        String order = "name DESC";

        Cursor cursor = db.query("pets", null, selection, selectionArgs, null, null, order);

        while (cursor != null && cursor.moveToNext()) {
            list.add(new Pet(cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getString(4),
                    cursor.getFloat(5),
                    cursor.getString(6),
                    cursor.getString(9),
                    cursor.getString(7),
                    cursor.getString(8))
            );
        }

        if (cursor != null)
            cursor.close();

        return list;
    }

    public Pet getById(int id) throws NullPointerException {
        Pet findPet = null;
        db = dbHelper.getReadableDatabase();

        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = null;
        try {
            cursor = db.query("pets", null, selection, selectionArgs, null, null, null);

            // Kiểm tra xem cursor có di chuyển đến bản ghi hay không
            if (cursor != null && cursor.moveToFirst()) {
                findPet = new Pet(cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getFloat(5),
                        cursor.getString(6),
                        cursor.getString(9),
                        cursor.getString(7),
                        cursor.getString(8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return findPet;
    }

    public long add(Pet pet) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", pet.getUserId());
        values.put("name", pet.getName());
        values.put("age", pet.getAge());
        values.put("breed", pet.getBreed());
        values.put("weight", pet.getWeight());
        values.put("device_address", pet.getDeviceAddress());
        values.put("image_path", pet.getImagePath());
        values.put("note", pet.getNote());

        return db.insert("pets", null, values);
    }

    public int update(Pet pet) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("user_id", pet.getUserId());
        values.put("name", pet.getName());
        values.put("age", pet.getAge());
        values.put("breed", pet.getBreed());
        values.put("weight", pet.getWeight());
        values.put("device_address", pet.getDeviceAddress());
        values.put("image_path", pet.getImagePath());
        values.put("note", pet.getNote());

        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(pet.getId())};

        return db.update("pets", values, whereClause, whereArgs);
    }

}
