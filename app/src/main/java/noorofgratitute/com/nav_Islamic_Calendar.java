package noorofgratitute.com;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class nav_Islamic_Calendar extends AppCompatActivity {
    private FusedLocationProviderClient fusedLocationClient;
    private CalendarView calendarView;
    private TextView txtHijriDate, txtGregorianDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_islamic_calendar);
        // Initialize Views
        calendarView = findViewById(R.id.calendarView);
        txtHijriDate = findViewById(R.id.txtHijriDate);
        txtGregorianDate = findViewById(R.id.txtGregorianDate);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        // Location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
        // Calendar Date Change Listener
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String gregorianDate = String.format("%02d-%02d-%d", dayOfMonth, (month + 1), year);
            txtGregorianDate.setText("📅 Gregorian Date: " + gregorianDate);
            getHijriDateFromAPI(gregorianDate);
        });
    }
    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                Toast.makeText(this, "📍 Location fetched", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getHijriDateFromAPI(String gregorianDate) {
        AladhanApiService apiService = RetrofitClient.getAladhanClient().create(AladhanApiService.class);
        Call<HijriResponse> call = apiService.getHijriDate(gregorianDate);

        call.enqueue(new Callback<HijriResponse>() {
            @Override
            public void onResponse(Call<HijriResponse> call, Response<HijriResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HijriResponse.Hijri hijri = response.body().data.hijri;
                    String hijriFormatted = hijri.day + " " + hijri.month.en + " " + hijri.year;
                    txtHijriDate.setText("🕋 Hijri Date: " + hijriFormatted);
                } else {
                    txtHijriDate.setText("🕋 Hijri Date: Error fetching");
                }
            }

            @Override
            public void onFailure(Call<HijriResponse> call, Throwable t) {
                txtHijriDate.setText("🕋 Hijri Date: Failed");
            }
        });
    }

    // Permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocation();
        } else {
            Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
        }
    }
}
