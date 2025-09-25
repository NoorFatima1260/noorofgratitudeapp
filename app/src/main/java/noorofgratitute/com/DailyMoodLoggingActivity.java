package noorofgratitute.com;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailyMoodLoggingActivity extends AppCompatActivity {
    private DailyMoodDAO dailyMoodDAO;
    private RadioGroup radioGroupMood;
    private EditText editTextCustomMood;
    private Button btnSaveMood;
    private TextView textViewIslamicReminder;
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
        setContentView(R.layout.activity_daily_mood_logging);
        radioGroupMood = findViewById(R.id.radioGroupMood);
        editTextCustomMood = findViewById(R.id.editTextCustomMood);
        btnSaveMood = findViewById(R.id.btnSaveMood);
        textViewIslamicReminder = findViewById(R.id.textViewIslamicReminder);
        btnBack = findViewById(R.id.btnBack);
        dailyMoodDAO = new DailyMoodDAO(this);
        editTextCustomMood.setVisibility(View.GONE);
        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(MoodSyncWorker.class, 15, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "MoodSyncWork",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
        );
        radioGroupMood.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtonHappy) {
                    showIslamicReminder("When you're happy, thank Allah. 'Indeed, little thanks do you give.' (Quran 23:78)");
                    editTextCustomMood.setVisibility(View.GONE);
                } else if (checkedId == R.id.radioButtonSad) {
                    showIslamicReminder("Turn to Allah in sadness. 'With every difficulty, there is relief.' (Quran 94:6)");
                    editTextCustomMood.setVisibility(View.GONE);
                } else if (checkedId == R.id.radioButtonNeutral) {
                    showIslamicReminder("Patience brings ease. 'Where there is hardship, there is also ease.' (Quran 94:7)");
                    editTextCustomMood.setVisibility(View.GONE);
                } else if (checkedId == R.id.radioButtonCustom) {
                    editTextCustomMood.setVisibility(View.VISIBLE);
                    showIslamicReminder("Reflect on your mood and turn to Allah for guidance.");
                }
            }
        });
// after local insert
        btnSaveMood.setOnClickListener(v -> {
            String selectedMood = "";

            int selectedId = radioGroupMood.getCheckedRadioButtonId();
            if (selectedId == R.id.radioButtonHappy) selectedMood = "😊";
            else if (selectedId == R.id.radioButtonSad) selectedMood = "😞";
            else if (selectedId == R.id.radioButtonNeutral) selectedMood = "😐";
            else if (selectedId == R.id.radioButtonCustom)
                selectedMood = editTextCustomMood.getText().toString().trim();
            if (selectedMood.isEmpty()) {
                showIslamicReminder("Please select or enter a mood.");
                return;
            }
            final String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            final String moodToSend = selectedMood;
            dailyMoodDAO.open();
            dailyMoodDAO.insertMood(currentDate, selectedMood);
            dailyMoodDAO.close();
            showIslamicReminder("Your mood: " + selectedMood + ". Remember to thank Allah.");
            // Get Firebase ID token
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                user.getIdToken(true).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String idToken = task.getResult().getToken();
                        // Send mood to Django backend
                        MoodEntry moodEntry = new MoodEntry(currentDate, moodToSend);
                        MoodApi api = RetrofitClientMood.getClient().create(MoodApi.class);
                        Call<MoodEntry> call = api.submitMood("Bearer " + idToken, moodEntry);
                        call.enqueue(new Callback<MoodEntry>() {
                            @Override
                            public void onResponse(Call<MoodEntry> call, Response<MoodEntry> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(DailyMoodLoggingActivity.this, "Mood synced online!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(DailyMoodLoggingActivity.this, "Server error. Mood saved locally.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<MoodEntry> call, Throwable t) {
                                Toast.makeText(DailyMoodLoggingActivity.this, "No internet. Mood saved locally.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(DailyMoodLoggingActivity.this, "Firebase authentication failed. Mood saved locally.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
        //show islamic reminder
    private void showIslamicReminder(String reminder) {
        textViewIslamicReminder.setText(reminder);
    }

}