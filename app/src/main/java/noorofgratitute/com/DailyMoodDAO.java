package noorofgratitute.com;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
public class DailyMoodDAO {
    private SQLiteDatabase database;
    private dbmainsqlit dbHelper;
    public DailyMoodDAO(Context context) {
        dbHelper = new dbmainsqlit(context);
    }
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();}
    public void close() {
        dbHelper.close();
    }
    public void insertMood(String date, String mood) {
        ContentValues values = new ContentValues();
        values.put(dbmainsqlit.COLUMN_MOOD_DATE, date);
        values.put(dbmainsqlit.COLUMN_MOOD, mood);
        database.insert(dbmainsqlit.TABLE_DAILY_MOOD, null, values);}
    public List<MoodEntry> getAllMoods() {
        List<MoodEntry> moods = new ArrayList<>();
        Cursor cursor = database.query(dbmainsqlit.TABLE_DAILY_MOOD,
                new String[]{dbmainsqlit.COLUMN_MOOD_ID, dbmainsqlit.COLUMN_MOOD_DATE, dbmainsqlit.COLUMN_MOOD},
                null, null, null, null, dbmainsqlit.COLUMN_MOOD_DATE + " DESC");
        while (cursor.moveToNext()) {
            moods.add(new MoodEntry(cursor.getString(1), cursor.getString(2)));}
        cursor.close();
        return moods;}
    String CREATE_MOODS_TABLE = "CREATE TABLE IF NOT EXISTS moods (" +
            "date TEXT PRIMARY KEY, " +
            "mood TEXT, " +
            "isSynced INTEGER DEFAULT 0)";

    public void markAsSynced(String date) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();  // <-- 'this' ko 'dbHelper' se replace karein
        ContentValues values = new ContentValues();
        values.put("isSynced", 1);
        db.update("moods", values, "date=?", new String[]{date});
    }

    public List<MoodEntry> getUnsyncedMoods() {
        List<MoodEntry> moods = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();  // <-- 'this' ko 'dbHelper' se replace karein
        Cursor cursor = db.rawQuery("SELECT date, mood FROM moods WHERE isSynced=0", null);
        if (cursor.moveToFirst()) {
            do {
                moods.add(new MoodEntry(cursor.getString(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return moods;
    }

}
