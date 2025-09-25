package noorofgratitute.com;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PrayerDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "prayer_times.db";
    private static final int DATABASE_VERSION = 5;  // bumped version

    public static final String TABLE_NAME = "prayer_times";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_ALARM_SET = "alarm_set";
    public static final String COLUMN_SYNC_STATUS = "sync_status";

    public PrayerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_NAME + " TEXT, " +
                COLUMN_TIME + " TEXT, " +
                COLUMN_ALARM_SET + " INTEGER DEFAULT 0, " +
                COLUMN_SYNC_STATUS + " INTEGER DEFAULT 0, " +
                "fiqh INTEGER, " +
                "PRIMARY KEY (" + COLUMN_NAME + ", fiqh))";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Purana table drop karke naya banado
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Function to insert or update prayer time and alarm state
    public void insertOrUpdatePrayerTime(String name, String time, boolean alarmSet) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_ALARM_SET, alarmSet ? 1 : 0); // Store alarm state as 1 or 0
        values.put(COLUMN_SYNC_STATUS, SyncStatus.SYNC_SUCCESS.value);

        long result = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        if (result == -1) {
            Log.e("DB_ERROR", "Failed to insert prayer time: " + name);
        } else {
            Log.d("DB_SUCCESS", "Inserted/Updated prayer time: " + name);
        }
    }

    public enum SyncStatus {
        SYNC_SUCCESS(1);
        private final int value;
        SyncStatus(int value) {
            this.value = value;
        }
    }

    public List<PrayerTime> getAllPrayerTimes() {
        List<PrayerTime> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY CASE " +
                "WHEN name = 'Fajr' THEN 1 " +
                "WHEN name = 'Dhuhr' THEN 2 " +
                "WHEN name = 'Asr' THEN 3 " +
                "WHEN name = 'Maghrib' THEN 4 " +
                "WHEN name = 'Isha' THEN 5 " +
                "ELSE 6 END", null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME));
                boolean alarmSet = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ALARM_SET)) == 1;
                list.add(new PrayerTime(name, time, alarmSet));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
