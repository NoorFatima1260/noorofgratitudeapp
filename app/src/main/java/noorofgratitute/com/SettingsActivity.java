package noorofgratitute.com;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
public class SettingsActivity extends AppCompatActivity {
    private RecyclerView settingsRecyclerView;
    private SettingsAdapter settingsAdapter;
    private List<SettingsItem> settingsList;
private ImageButton btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        btnBack = findViewById(R.id.btnBack);
        //list
        settingsList = new ArrayList<>();
        settingsList.add(new SettingsItem("General Settings", true));
        settingsList.add(new SettingsItem("Profile", false));
        settingsList.add(new SettingsItem("Notifications", false));

         //recyclerView
        settingsRecyclerView = findViewById(R.id.settings_recycler_view);
        settingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        settingsAdapter = new SettingsAdapter(this, settingsList);
        settingsRecyclerView.setAdapter(settingsAdapter);
        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    } }
