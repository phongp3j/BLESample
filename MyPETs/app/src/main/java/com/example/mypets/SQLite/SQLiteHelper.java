package com.example.mypets.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.mypets.Model.Pet;
import com.example.mypets.Model.User;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "User.db";
    private static int DATABASE_VERSION = 1;
    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE users("+"id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "username TEXT,password TEXT,fullname TEXT, email TEXT,phone TEXT)";
        db.execSQL(sql);
        String sql1 = "CREATE TABLE pets("+"id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "name TEXT,type TEXT,age INTEGER,addressDevice TEXT, userId INTEGER)";
        db.execSQL(sql1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
    //Register
    public long register(User i){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        // Kiểm tra xem username đã tồn tại hay chưa
        String[] columns = {"username"};
        String selection = "username = ?";
        String[] selectionArgs = {i.getUsername()};
        Cursor cursor = sqLiteDatabase.query("users", columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            // Username đã tồn tại
            cursor.close();
            return -1; // Trả về giá trị -1 nếu người dùng đã tồn tại
        }
        cursor.close();
        ContentValues values = new ContentValues();
        values.put("username",i.getUsername());
        values.put("password",i.getPassword());
        values.put("fullname",i.getFullname());
        values.put("email",i.getEmail());
        values.put("phone",i.getPhone());
        return sqLiteDatabase.insert("users",null,values);
    }

    //Login
    public boolean login(String username,String password){
        String whereClause = "username like ?";
        String[] whereArgs = {username};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor rs = sqLiteDatabase.query("users",null,whereClause,whereArgs,null,null,null);
        while (rs!=null && rs.moveToNext()){
            int id = rs.getInt(0);
            String username1 = rs.getString(1);
            String password1 = rs.getString(2);
            String fullname = rs.getString(3);
            String email = rs.getString(4);
            String phone = rs.getString(5);
            if(username.equals(username1) && password.equals(password1)){
                return true;
            }
        }
        return false;
    }

    //Get all pet
    public List<Pet> getAllPet(String userId){
        List<Pet> list = new ArrayList<>();
        SQLiteDatabase st = getReadableDatabase();
        String order = "name DESC";
        Cursor rs = st.query("pets",null,null,null,null,null,order);
        while (rs!=null && rs.moveToNext()){
            int id = rs.getInt(0);
            String name = rs.getString(1);
            String type = rs.getString(2);
            int age = rs.getInt(3);
            String addressDevice = rs.getString(4);
            String userId1 = rs.getString(5);
            if(userId1.equals(userId)){
                list.add(new Pet(id,name,type,age,addressDevice,userId1));
            }
        }
        return list;
    }
    //Add pet
    //Add item
    public long addPet(Pet i){
        ContentValues values = new ContentValues();
        values.put("name",i.getName());
        values.put("type",i.getType());
        values.put("age",i.getAge());
        values.put("addressDevice",i.getAddressDevice());
        values.put("userId",i.getUserid());
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.insert("pets",null,values);
    }

}
