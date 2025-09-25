package noorofgratitute.com;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrayerActivity extends AppCompatActivity {
    private RecyclerView recyclerPrayerTimes;
    private PrayerTimesAdapter prayerTimesAdapter;
    private List<PrayerTime> prayerTimesList;
    private AladhanApiService apiService;
    private ImageButton btnBack;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private PrayerDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer);

        dbHelper = new PrayerDbHelper(this);
        TextView nextPrayerText = findViewById(R.id.txtNextPrayer);
        nextPrayerText.setVisibility(View.VISIBLE);

        recyclerPrayerTimes = findViewById(R.id.recyclerPrayerTimes);
        recyclerPrayerTimes.setLayoutManager(new LinearLayoutManager(this));
        prayerTimesList = new ArrayList<>();
        prayerTimesAdapter = new PrayerTimesAdapter(this, prayerTimesList);
        recyclerPrayerTimes.setAdapter(prayerTimesAdapter);

        btnBack = findViewById(R.id.btnBack);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        apiService = RetrofitClient.getAladhanClient().create(AladhanApiService.class);

        // Load cached data
        List<PrayerTime> cachedTimes = dbHelper.getAllPrayerTimes();
        if (!cachedTimes.isEmpty()) {
            prayerTimesList.clear();
            prayerTimesList.addAll(cachedTimes);
            prayerTimesAdapter.notifyDataSetChanged();
            updateNextPrayer();
        }
        getLocationAndFetchPrayerTimes();
        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }
    private void getLocationAndFetchPrayerTimes() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                fetchPrayerTimes(location.getLatitude(), location.getLongitude());
            } else {
                Toast.makeText(PrayerActivity.this, "Location not found!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateNextPrayer() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Calendar now = Calendar.getInstance();

        PrayerTime nextPrayer = null;
        long minDiff = Long.MAX_VALUE;

        for (PrayerTime prayer : prayerTimesList) {
            try {
                Date time = sdf.parse(prayer.getTime());
                if (time == null) continue;

                Calendar prayerTime = Calendar.getInstance();
                Calendar tempCal = Calendar.getInstance();
                tempCal.setTime(time);

                prayerTime.set(Calendar.HOUR_OF_DAY, tempCal.get(Calendar.HOUR_OF_DAY));
                prayerTime.set(Calendar.MINUTE, tempCal.get(Calendar.MINUTE));
                prayerTime.set(Calendar.SECOND, 0);

                if (prayerTime.before(now)) continue;

                long diff = prayerTime.getTimeInMillis() - now.getTimeInMillis();
                if (diff < minDiff) {
                    minDiff = diff;
                    nextPrayer = prayer;
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }}

            TextView nextPrayerText = findViewById(R.id.txtNextPrayer);
        if (nextPrayer != null) {
            nextPrayerText.setText("Next Prayer: " + nextPrayer.getName() + " - " + nextPrayer.getTime());
            startCountdown(minDiff, nextPrayer.getName());
        } else {
            nextPrayerText.setText("All prayers done for today.");
        }
    }

    //accept prayer name
    private void startCountdown(long millis, String prayerName) {
        new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long ms) {
                long seconds = ms / 1000;
                long minutes = seconds / 60;
                seconds %= 60;
                long hours = minutes / 60;
                minutes %= 60;

                String timeLeft = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                TextView countdownText = findViewById(R.id.txtNextPrayer);
                countdownText.setText("Next Prayer in: " + timeLeft + " (" + prayerName + ")");
            }

            @Override
            public void onFinish() {
                TextView countdownText = findViewById(R.id.txtNextPrayer);
                countdownText.setText("It's time for " + prayerName);
            }
        }.start();
    }

    private void fetchPrayerTimes(double lat, double lon) {
        apiService.getPrayerTimes(lat, lon, 2).enqueue(new Callback<PrayerResponse>() {
            @Override
            public void onResponse(Call<PrayerResponse> call, Response<PrayerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PrayerResponse data = response.body();
                    runOnUiThread(() -> {
                        Map<String, String> timings = data.getData().getTimings().getAllTimingsIn12HourFormat();

                        // Preserve alarm state
                        List<PrayerTime> existingTimes = dbHelper.getAllPrayerTimes();

                        prayerTimesList.clear();
                        String[] orderedPrayerNames = {"Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"};
                        for (String prayerName : orderedPrayerNames) {
                            String prayerTime = timings.get(prayerName);
                            if (prayerTime == null) continue;

                            boolean alarmSet = false;
                            for (PrayerTime existing : existingTimes) {
                                if (existing.getName().equalsIgnoreCase(prayerName)) {
                                    alarmSet = existing.isAlarmSet();
                                    break;
                                }
                            }
                            prayerTimesList.add(new PrayerTime(prayerName, prayerTime, alarmSet));
                            dbHelper.insertOrUpdatePrayerTime(prayerName, prayerTime, alarmSet);
                        }
                        prayerTimesAdapter.notifyDataSetChanged();
                        updateNextPrayer();
                    });
                }
            }
            @Override
            public void onFailure(Call<PrayerResponse> call, Throwable t) {
                Toast.makeText(PrayerActivity.this, "Error fetching prayer times", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

