package noorofgratitute.com;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "IslamicEvents.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_EVENTS = "IslamicEvents";
    private static final String COL_DATE = "hijri_date";
    private static final String COL_EVENT = "event_name";
    private static DatabaseHelper instance;
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());}
        return instance;}
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);}
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_EVENTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " TEXT, " +
                COL_EVENT + " TEXT)");}
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);}
    public void insertOrUpdateEvent(String hijriDate, String eventName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EVENTS + " WHERE " + COL_DATE + " = ?", new String[]{hijriDate});
        if (cursor.moveToFirst()) {
            db.execSQL("UPDATE " + TABLE_EVENTS + " SET " + COL_EVENT + " = ? WHERE " + COL_DATE + " = ?",
                    new String[]{eventName, hijriDate});
        } else {
            db.execSQL("INSERT INTO " + TABLE_EVENTS + " (" + COL_DATE + ", " + COL_EVENT + ") VALUES (?, ?)",
                    new String[]{hijriDate, eventName});}
        cursor.close();}
    public String getEventByHijriDate(String hijriDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_EVENT + " FROM " + TABLE_EVENTS + " WHERE " + COL_DATE + " = ?", new String[]{hijriDate});
        if (cursor.moveToFirst()) {
            String event = cursor.getString(0);
            cursor.close();
            return event;}
        cursor.close();
        return null;}
    public List<String> getAllEvents() {
        List<String> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_DATE + ", " + COL_EVENT + " FROM " + TABLE_EVENTS, null);

        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(0);
                String event = cursor.getString(1);
                eventList.add(date + " - " + event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return eventList;
    }
    public List<String> searchEvents(String query) {
        List<String> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_DATE + ", " + COL_EVENT + " FROM " + TABLE_EVENTS +
                        " WHERE " + COL_EVENT + " LIKE ? OR " + COL_DATE + " LIKE ?",
                new String[]{"%" + query + "%", "%" + query + "%"});
        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(0);
                String event = cursor.getString(1);
                eventList.add(date + " - " + event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return eventList;
    }
}
