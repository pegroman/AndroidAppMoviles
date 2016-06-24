package com.example.pegroman.tets;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ITEMS";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE2="CREATE TABLE Ubicacion (id integer primary key autoincrement,latitud integer, longitud integer, idPersona integer);";
    private static final String DATABASE_CREATE = "CREATE TABLE Item (id integer primary key ,nombre text,apellido text, foto blob, fecha text,ubicacion text);";
    private static final String DATABASE_CREATE3="CREATE TABLE User(id integer primary key autoincrement not null, nombre text, contrasenia text);";

    private SQLiteDatabase bd;
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        try {
            db.execSQL(DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE2);
            db.execSQL(DATABASE_CREATE3);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Item");
        db.execSQL("DROP TABLE IF EXISTS Ubicacion");
        db.execSQL("SROP TABLE IF EXISTS User");
        onCreate(db);
    }

    public SQLiteDatabase open() {
        bd = this.getWritableDatabase();
        return bd;
    }

    public void close() {
        bd.close();
    }
}