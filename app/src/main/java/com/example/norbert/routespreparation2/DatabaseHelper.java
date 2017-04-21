package com.example.norbert.routespreparation2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "tabela_tras";
    private static final String COL1 = "ID";
    private static final String COL2 = "start";
    private static final String COL3 = "koniec";
    private static final String COL4 = "czas";

    public String getStartP() {
        return StartP;
    }

    public void setStartP(String startP) {
        StartP = startP;
    }

    public String getKoniecP() {
        return KoniecP;
    }

    public void setKoniecP(String koniecP) {
        KoniecP = koniecP;
    }

    public String getCzasP() {
        return CzasP;
    }

    public void setCzasP(String czasP) {
        CzasP = czasP;
    }

    public String StartP, KoniecP, CzasP;

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL2 + " TEXT," + COL3 + " TEXT," + COL4 + " TEXT)";
        sqLiteDatabase.execSQL(createTable);
    }

    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP IF TABLE EXIST " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean addData() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, getStartP());
        contentValues.put(DatabaseHelper.COL3, getKoniecP());
        contentValues.put(DatabaseHelper.COL4, getCzasP());

        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String querry = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(querry, null);
        return data;
    }
}
