package com.example.mypets.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mypets.Model.User;
import com.example.mypets.Utils.PasswordUtils;

public class UserDao {

    private final SQLiteHelper dbHelper;
    private SQLiteDatabase db;

    public UserDao(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void close() {
        dbHelper.close();

        if (db != null)
            db.close();
    }

    //Register
    public long register(User user) {
        db = dbHelper.getReadableDatabase();

        // Kiểm tra xem username đã tồn tại hay chưa
        String[] columns = {"username"};
        String selection = "username = ?";
        String[] selectionArgs = {user.getUsername()};

        Cursor cursor = db.query("users", columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            // Username đã tồn tại
            cursor.close();
            return -1; // Trả về giá trị -1 nếu người dùng đã tồn tại
        }

        cursor.close();
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("password", user.getHashedPassword());
        return db.insert("users", null, values);
    }

    //Login
    public int login(String username, String rawPassword) {
        String whereClause = "username like ?";
        String[] whereArgs = {username};

        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("users", null, whereClause, whereArgs, null, null, null);

        int userId = -1;    // not found
        while (cursor != null && cursor.moveToNext()) {
            String hashedPassword = PasswordUtils.hashPassword(rawPassword);

            String fUsername = cursor.getString(1);
            String fHashedPassword = cursor.getString(2);

            if (username.equals(fUsername) && hashedPassword.equals(fHashedPassword)) {
                userId = cursor.getInt(0);
            }
        }

        if (cursor != null)
            cursor.close();

        return userId;
    }

}
