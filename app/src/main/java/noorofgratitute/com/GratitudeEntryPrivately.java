package noorofgratitute.com;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
public class GratitudeEntryPrivately {
    private SQLiteDatabase database;
    private dbmainsqlit dbHelper;
    public GratitudeEntryPrivately(Context context) {
        dbHelper = new dbmainsqlit(context);
    }
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        if (database == null || !database.isOpen()) {
            throw new SQLException("Database could not be opened!");}}
    //close Database connection
    public void close() {
        dbHelper.close();
    }

    //insert gratitude entry private
    public long insertGratitudeEntryPrivately(String date, String gratitudeText) {
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("gratitude_text", gratitudeText);
        return database.insert("gratitude_entries_private", null, values);}
    //retrieve all private gratitude entries
    public List<GratitudeEntrypri> getAllPrivateEntries() {
        List<GratitudeEntrypri> entries = new ArrayList<>();
        Cursor cursor = database.query("gratitude_entries_private",
                new String[]{"id", "date", "gratitude_text"},
                null, null, null, null, "date DESC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String date = cursor.getString(1);
                String gratitudeText = cursor.getString(2);
                entries.add(new GratitudeEntrypri(id, date, gratitudeText));
            } while (cursor.moveToNext());
            cursor.close();}
        return entries;}

   }
