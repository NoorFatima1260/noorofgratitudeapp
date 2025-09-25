package noorofgratitute.com;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class dbmainsqlit extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "NoorOfGratitude.db";
    private static final int DATABASE_VERSION = 2;
    //tables names
    public static final String TABLE_DAILY_MOOD = "daily_mood";
    public static final String TABLE_GRATITUDE = "gratitude_entries";
    public static final String TABLE_GRATITUDE_PRIVATE = "gratitude_entries_private";

    //mood loging
    public static final String COLUMN_MOOD_ID = "id";
    public static final String COLUMN_MOOD_DATE = "date";
    public static final String COLUMN_MOOD = "mood";
    public static final String COLUMN_IS_SYNCED = "is_synced";
    //gratitude column
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TEXT = "gratitude_text";
    public static final String COLUMN_IS_PUBLIC = "is_public";
    //create table queries
    private static final String CREATE_TABLE_DAILY_MOOD = "CREATE TABLE " +
            TABLE_DAILY_MOOD + " (" +
            COLUMN_MOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_MOOD_DATE + " TEXT NOT NULL, " +
            COLUMN_MOOD + " TEXT NOT NULL, " +
            COLUMN_IS_SYNCED + " INTEGER DEFAULT 0" +
            ");";

    private static final String CREATE_TABLE_GRATITUDE_PRIVATE = "CREATE TABLE " +
            TABLE_GRATITUDE_PRIVATE + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DATE + " TEXT NOT NULL, " +
            COLUMN_TEXT + " TEXT NOT NULL" +
            ");";
    private static final String CREATE_TABLE_GRATITUDE = "CREATE TABLE " +
            TABLE_GRATITUDE + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DATE + " TEXT NOT NULL, " +
            COLUMN_TEXT + " TEXT NOT NULL, " +
            COLUMN_IS_PUBLIC + " INTEGER NOT NULL DEFAULT 0" +
            ");";

    public dbmainsqlit(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DAILY_MOOD);
        db.execSQL(CREATE_TABLE_GRATITUDE);
        db.execSQL(CREATE_TABLE_GRATITUDE_PRIVATE);}
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAILY_MOOD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GRATITUDE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GRATITUDE_PRIVATE);
        onCreate(db);}
}
