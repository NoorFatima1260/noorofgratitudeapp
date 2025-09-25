package noorofgratitute.com;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
public class PrivacyActivity extends AppCompatActivity {
    private Switch switchPrivateMode;
    private Switch switchDataSharing;
    private Switch switchLocationAccess;
    private ImageButton btnBack;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        btnBack = findViewById(R.id.btnBack);
        //back navigation in the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Privacy Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); }
        sharedPreferences = getSharedPreferences("PrivacyPrefs", MODE_PRIVATE);
        //switches
        switchPrivateMode = findViewById(R.id.switch_private_mode);
        switchDataSharing = findViewById(R.id.switch_data_sharing);
        switchLocationAccess = findViewById(R.id.switch_location_access);
        loadPreferences();
        //listeners for switches
        switchPrivateMode.setOnCheckedChangeListener(this::onSwitchChanged);
        switchDataSharing.setOnCheckedChangeListener(this::onSwitchChanged);
        switchLocationAccess.setOnCheckedChangeListener(this::onSwitchChanged);
        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }
    private void onSwitchChanged(CompoundButton buttonView, boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (buttonView.getId() == R.id.switch_private_mode) {
            editor.putBoolean("private_mode", isChecked);
        } else if (buttonView.getId() == R.id.switch_data_sharing) {
            editor.putBoolean("data_sharing", isChecked);
        } else if (buttonView.getId() == R.id.switch_location_access) {
            editor.putBoolean("location_access", isChecked);
        }
        editor.apply(); }
    private void loadPreferences() {
        switchPrivateMode.setChecked(sharedPreferences.getBoolean("private_mode", false));
        switchDataSharing.setChecked(sharedPreferences.getBoolean("data_sharing", false));
        switchLocationAccess.setChecked(sharedPreferences.getBoolean("location_access", false));
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true; } }
