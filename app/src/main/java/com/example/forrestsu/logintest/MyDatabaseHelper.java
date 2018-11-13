package com.example.forrestsu.logintest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_BOOK = "CREATE TABLE Book ("
            + "id integer PRIMARY KEY AUTOINCREMENT, "
            + "author text, "
            + "price real, "
            + "page integer, "
            + "name text)";

    public static final String CREATE_CATEGORY = "CREATE TABLE Category ("
            + "id integer PRIMARY KEY AUTOINCREMENT, "
            + "category_name text, "
            + "category_code integer)";

    private Context mContext;
    public MyDatabaseHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK);
        db.execSQL(CREATE_CATEGORY);
        Toast.makeText(mContext, "创建成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Book");
        db.execSQL("DROP TABLE IF EXISTS Category");
        onCreate(db);
    }
}
