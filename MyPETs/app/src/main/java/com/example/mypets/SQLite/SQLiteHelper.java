package com.example.mypets.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "User.db";
    private static final int DATABASE_VERSION = 1;

    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng Users
        String CREATE_USERS_TABLE = "CREATE TABLE Users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username VARCHAR(100) NOT NULL UNIQUE," +
                "password VARCHAR(255) NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Tạo bảng Pets
        String CREATE_PETS_TABLE = "CREATE TABLE Pets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "name VARCHAR(100) NOT NULL," +
                "age INTEGER NOT NULL," +
                "breed VARCHAR(100)," +
                "weight FLOAT," +
                "deviceAddress VARCHAR(50) NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE" +
                ")";
        db.execSQL(CREATE_PETS_TABLE);

        // Tạo bảng HealthData
        String CREATE_HEALTHDATA_TABLE = "CREATE TABLE HealthData (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "device_id INTEGER NOT NULL," +
                "heart_rate INTEGER," +
                "temperature FLOAT," +
                "recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (device_id) REFERENCES Pets(id) ON DELETE CASCADE" +
                ")";
        db.execSQL(CREATE_HEALTHDATA_TABLE);

        // Tạo bảng Reminders
        String CREATE_REMINDERS_TABLE = "CREATE TABLE Reminders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "pet_id INTEGER NOT NULL," +
                "type VARCHAR(100) NOT NULL," +
                "date DATE NOT NULL," +
                "time TIME NOT NULL," +
                "has_done INTEGER NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (pet_id) REFERENCES Pets(id) ON DELETE CASCADE" +
                ")";
        db.execSQL(CREATE_REMINDERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa các bảng cũ nếu chúng đã tồn tại
        db.execSQL("DROP TABLE IF EXISTS Users");
        db.execSQL("DROP TABLE IF EXISTS Pets");
        db.execSQL("DROP TABLE IF EXISTS HealthData");
        db.execSQL("DROP TABLE IF EXISTS Reminders");

        // Tạo lại các bảng mới
        onCreate(db);
    }

}
