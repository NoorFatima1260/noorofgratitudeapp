package noorofgratitute.com;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import java.util.List;
public class gratitudeprogress_tracker extends AppCompatActivity {
    private CircularProgressIndicator progressCircular;
    private TextView tvProgressPercent, tvTotalEntries, tvPrivateEntries, tvPublicEntries, tvStreakCount, tvLastEntry, tvMotivation;
    private ImageButton btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AuthUtils.isUserLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_gratitudeprogress_tracker);
        initViews();
        loadProgress();
        btnBack.setOnClickListener(v -> onBackPressed());}
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        progressCircular = findViewById(R.id.progress_circular);
        tvProgressPercent = findViewById(R.id.tv_progress_percent);
        tvTotalEntries = findViewById(R.id.tv_total_entries);
        tvPrivateEntries = findViewById(R.id.tv_private_entries);
        tvPublicEntries = findViewById(R.id.tv_public_entries);
        tvStreakCount = findViewById(R.id.tv_streak_count);
        tvLastEntry = findViewById(R.id.tv_last_entry);
        tvMotivation = findViewById(R.id.tv_motivation);
        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });}
    private void loadProgress() {
        SharedPreferences sharedPreferences = getSharedPreferences("GratitudePrefs", MODE_PRIVATE);
        int streakCount = sharedPreferences.getInt("StreakCount", 0);
        //SQLite data
        GratitudeEntryPrivately privateDao = new GratitudeEntryPrivately(this);
        privateDao.open();
        List<GratitudeEntrypri> privateEntries = privateDao.getAllPrivateEntries();
        privateDao.close();
        //public count retrieved from Firebase)
        int publicEntriesCount = sharedPreferences.getInt("PublicGratitudeCount", 0);
        int total = privateEntries.size() + publicEntriesCount;
        //Set texts
        tvTotalEntries.setText("Total Entries: " + total);
        tvPrivateEntries.setText("Private: " + privateEntries.size());
        tvPublicEntries.setText("Public: " + publicEntriesCount);
        tvStreakCount.setText("Current Streak: " + streakCount + " days");
        Log.d("DEBUG", "progressCircular is " + (progressCircular == null ? "null" : "not null"));

        if (!privateEntries.isEmpty()) {
            tvLastEntry.setText("Last Entry: " + privateEntries.get(0).getGratitudeText());
        } else {
            tvLastEntry.setText("Last Entry: None");}
        // Progress bar streak out of 30 days
        if (streakCount == 0) {
            progressCircular.setIndeterminate(false);
            progressCircular.setProgress(0);
            tvProgressPercent.setText("Start Now!");
        } else {
            int progress = Math.min((streakCount * 100) / 30, 100);
            if (progress < 5) progress = 5;
            progressCircular.setProgress(progress);
            tvProgressPercent.setText(progress + "%");
        }


        //motivation from Al-Quran Shareef
        if (streakCount == 0) {
            tvMotivation.setText("“Start today with Bismillah and gratitude.”");
        } else if (streakCount < 7) {
            tvMotivation.setText("“Gratitude is the best attitude.”");
        } else if (streakCount < 30) {
            tvMotivation.setText("“Patience and gratitude increase blessings.”");
        } else {
            tvMotivation.setText("“MashaAllah! Keep the spirit alive.”");
        }}


    private void updateStreak() {
        SharedPreferences prefs = getSharedPreferences("GratitudePrefs", MODE_PRIVATE);
        long lastEntryTime = prefs.getLong("LastEntryTime", 0);
        long currentTime = System.currentTimeMillis();

        int currentStreak = prefs.getInt("StreakCount", 0);
        long oneDay = 24 * 60 * 60 * 1000;

        if (lastEntryTime > 0) {
            long diff = currentTime - lastEntryTime;
            if (diff >= oneDay && diff < 2 * oneDay) {
                currentStreak++;
            } else if (diff >= 2 * oneDay) {
                currentStreak = 1; // Reset streak
            }
        } else {
            currentStreak = 1;
        }

        prefs.edit()
                .putInt("StreakCount", currentStreak)
                .putLong("LastEntryTime", currentTime)
                .apply();
    }
}
