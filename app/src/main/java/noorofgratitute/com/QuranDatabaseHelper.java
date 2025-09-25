package noorofgratitute.com;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class QuranDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "quran.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_SURAH = "surah";
    public static final String TABLE_JUZ = "juz";

    public QuranDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSurahTable = "CREATE TABLE " + TABLE_SURAH + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "number INTEGER, " +
                "name TEXT, " +
                "pdf_path TEXT, " +
                "audio_path TEXT, " +
                "is_downloaded INTEGER DEFAULT 0)";

        String createJuzTable = "CREATE TABLE " + TABLE_JUZ + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "number INTEGER, " +
                "name TEXT, " +
                "pdf_path TEXT, " +
                "audio_path TEXT, " +
                "is_downloaded INTEGER DEFAULT 0)";

        db.execSQL(createSurahTable);
        db.execSQL(createJuzTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SURAH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JUZ);
        onCreate(db);
    }

    public void insertSurah(int number, String name, String pdfPath, String audioPath, int isDownloaded) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("name", name);
        values.put("pdf_path", pdfPath);
        values.put("audio_path", audioPath);
        values.put("is_downloaded", isDownloaded);
        db.insert(TABLE_SURAH, null, values);
        db.close();
    }

    public void insertJuz(int number, String name, String pdfPath, String audioPath, int isDownloaded) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("name", name);
        values.put("pdf_path", pdfPath);
        values.put("audio_path", audioPath);
        values.put("is_downloaded", isDownloaded);
        db.insert(TABLE_JUZ, null, values);
        db.close();
    }

    public Cursor getAllSurahs() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_SURAH + " WHERE is_downloaded = 1", null);
    }

    public Cursor getAllJuzs() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_JUZ + " WHERE is_downloaded = 1", null);
    }
}
