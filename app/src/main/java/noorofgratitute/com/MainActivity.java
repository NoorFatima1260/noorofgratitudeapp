package noorofgratitute.com;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class MainActivity extends AppCompatActivity {
    private View fragmentContainer, mainContent;
    private TextView verseReference, verseArabic, verseTranslation;
    private ImageButton playAudioButton;
    private MediaPlayer mediaPlayer;
    private String audioUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
                .addOnCompleteListener(task -> {
                    String msg = task.isSuccessful() ? "Subscribed to all_users" : "Subscription failed";
                    Log.d("FCM", msg);
                });
        setContentView(R.layout.activity_main);
        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        ImageButton pickDateButton = findViewById(R.id.btn_pick_date);

        pickDateButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        fetchVerseOfTheDay(selectedDate); // API call with date param
                    },
                    2025, 0, 1 // Default open on Jan 1, 2025
            );
            datePickerDialog.show();
        });

        //views
        fragmentContainer = findViewById(R.id.fragment_container);
        mainContent = findViewById(R.id.main_content);
        //Verse UI references —
        verseReference   = findViewById(R.id.verse_reference);
        verseArabic      = findViewById(R.id.verse_arabic);
        verseTranslation = findViewById(R.id.verse_translation);
        playAudioButton  = findViewById(R.id.play_audio_button);
        //fetch and display todays verse
        fetchVerseOfTheDay(null);
        //play audio when tapped
        playAudioButton.setOnClickListener(v -> {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(audioUrl);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    playAudioButton.setImageResource(android.R.drawable.ic_media_pause);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Playback error", Toast.LENGTH_SHORT).show();
                }
            } else if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playAudioButton.setImageResource(android.R.drawable.ic_media_play);
            } else {
                mediaPlayer.start();
                playAudioButton.setImageResource(android.R.drawable.ic_media_pause);
            }});
        //bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_calander) {
                startActivity(new Intent(MainActivity.this, nav_Islamic_Calendar.class));
                return true;
            } else if (item.getItemId() == R.id.nav_prayer) {
                startActivity(new Intent(MainActivity.this, PrayerActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_Qibla) {
                startActivity(new Intent(MainActivity.this, QiblaActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_more) {
                startActivity(new Intent(MainActivity.this, MoreQuranDailyBlessing.class));
                return true;
            }
            return false;
        });
        //card click listeners
        findViewById(R.id.card_gratitude_journal).setOnClickListener(v -> loadFragment(new fragment_gratitude_journal()));
        findViewById(R.id.card_mood_journal).setOnClickListener(v -> loadFragment(new FragmentOfMoodJournal()));
    }
    private void loadFragment(Fragment fragment) {
        mainContent.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();}
    //function verse of the day
    private void fetchVerseOfTheDay(String date) {
        ApiServiceverse api = ApiClientverse.getClient().create(ApiServiceverse.class);
        api.getVerseOfTheDay(date).enqueue(new Callback<VerseOfTheDay>() {
            @Override
            public void onResponse(Call<VerseOfTheDay> call, Response<VerseOfTheDay> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VerseOfTheDay v = response.body();
                    verseReference.setText(v.getReference());
                    verseArabic.setText(v.getArabic());
                    verseTranslation.setText(v.getTranslation());
                    audioUrl = v.getAudioUrl();
                } else {
                    Toast.makeText(MainActivity.this, "No verse found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VerseOfTheDay> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void playAudio(String url) {
        if (mediaPlayer != null) mediaPlayer.release();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show();
        } }
    @Override
    public void onBackPressed() {
        if (fragmentContainer.getVisibility() == View.VISIBLE) {
            fragmentContainer.setVisibility(View.GONE);
            mainContent.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        } }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true; }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (item.getItemId() == R.id.Log_in) {
            startActivity(new Intent(this, LoginActivity.class));
            return true; }
        return super.onOptionsItemSelected(item); }
    @Override protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null; }
        super.onDestroy();
    } }

