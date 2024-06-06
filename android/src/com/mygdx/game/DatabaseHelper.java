package com.mygdx.game;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "TechnoJump.db";

    private static final String TABLE_NAME_SINGLE_PLAYER = "singlePlayer";

    private static final String COLUMN_ID_SINGLE_PLAYER = "id";
    private static final String COLUMN_NAME_SINGLE_PLAYER = "name";
    private static final String COLUMN_SCORE_SINGLE_PLAYER = "score";

    // Create Table SQL Statement
    private static final String TABLE_CREATE_SINGLE_PLAYER =
            "CREATE TABLE " + TABLE_NAME_SINGLE_PLAYER + " (" +
                    COLUMN_ID_SINGLE_PLAYER + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME_SINGLE_PLAYER + " TEXT, " +
                    COLUMN_SCORE_SINGLE_PLAYER + " FLOAT);";

    private static final String TABLE_NAME_BLUETOOTH_PLAYER = "bluetoothPlayer";
    private static final String COLUMN_ID_BLUETOOTH = "id";
    private static final String COLUMN_NAME_FIRST_BLUETOOTH = "nameFirst";
    private static final String COLUMN_NAME_SECOND_BLUETOOTH = "nameSecond";
    private static final String COLUMN_NAME_FIRST_SCORE = "scoreFirst";
    private static final String COLUMN_NAME_SECOND_SCORE = "scoreSecond";

    private static final String TABLE_CREATE_BLUETOOTH_PLAYER =
            "CREATE TABLE " + TABLE_NAME_BLUETOOTH_PLAYER + " (" +
                    COLUMN_ID_BLUETOOTH + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME_FIRST_BLUETOOTH + " TEXT, " +
                    COLUMN_NAME_SECOND_BLUETOOTH + " TEXT, " +
                    COLUMN_NAME_FIRST_SCORE + " FLOAT, " +
                    COLUMN_NAME_SECOND_SCORE + " FLOAT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_SINGLE_PLAYER);
        db.execSQL(TABLE_CREATE_BLUETOOTH_PLAYER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SINGLE_PLAYER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_BLUETOOTH_PLAYER);
        onCreate(db);
    }

    public long addSinglePlayerScore(String name, float score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_SINGLE_PLAYER, name);
        values.put(COLUMN_SCORE_SINGLE_PLAYER, score);
        return db.insert(TABLE_NAME_SINGLE_PLAYER, null, values);
    }

    public Cursor getAllSinglePlayerScores() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME_SINGLE_PLAYER, null, null, null, null, null, null);
    }

    public int updateSinglePlayerScore(long id, String name, float score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_SINGLE_PLAYER, name);
        values.put(COLUMN_SCORE_SINGLE_PLAYER, score);
        return db.update(TABLE_NAME_SINGLE_PLAYER, values, COLUMN_ID_SINGLE_PLAYER + " = ?", new String[]{String.valueOf(id)});
    }

    public int deleteSinglePlayerScore(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME_SINGLE_PLAYER, COLUMN_ID_SINGLE_PLAYER + " = ?", new String[]{String.valueOf(id)});
    }

    public long addBluetoothPlayerScore(String nameFirst, String nameSecond, float scoreFirst, float scoreSecond) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_FIRST_BLUETOOTH, nameFirst);
        values.put(COLUMN_NAME_SECOND_BLUETOOTH, nameSecond);
        values.put(COLUMN_NAME_FIRST_SCORE, scoreFirst);
        values.put(COLUMN_NAME_SECOND_SCORE, scoreSecond);
        return db.insert(TABLE_NAME_BLUETOOTH_PLAYER, null, values);
    }

    public Cursor getAllBluetoothPlayerScores() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME_BLUETOOTH_PLAYER, null, null, null, null, null, null);
    }

    public int updateBluetoothPlayerScore(long id, String nameFirst, String nameSecond, float scoreFirst, float scoreSecond) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_FIRST_BLUETOOTH, nameFirst);
        values.put(COLUMN_NAME_SECOND_BLUETOOTH, nameSecond);
        values.put(COLUMN_NAME_FIRST_SCORE, scoreFirst);
        values.put(COLUMN_NAME_SECOND_SCORE, scoreSecond);
        return db.update(TABLE_NAME_BLUETOOTH_PLAYER, values, COLUMN_ID_BLUETOOTH + " = ?", new String[]{String.valueOf(id)});
    }

    public int deleteBluetoothPlayerScore(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME_BLUETOOTH_PLAYER, COLUMN_ID_BLUETOOTH + " = ?", new String[]{String.valueOf(id)});
    }
}