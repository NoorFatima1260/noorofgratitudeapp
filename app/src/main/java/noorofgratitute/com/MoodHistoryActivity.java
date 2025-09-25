package noorofgratitute.com;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.ArrayList;
import java.util.List;

public class MoodHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MoodHistoryAdapter adapter;
    private DailyMoodDAO dailyMoodDAO;
    private LineChart lineChart;
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
        setContentView(R.layout.activity_mood_history);
        btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recyclerViewMoodHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        lineChart = findViewById(R.id.lineChart);
        setupChart();
        dailyMoodDAO = new DailyMoodDAO(this);
        dailyMoodDAO.open();
        List<MoodEntry> moodEntries = dailyMoodDAO.getAllMoods();
        dailyMoodDAO.close();
        adapter = new MoodHistoryAdapter(moodEntries);
        recyclerView.setAdapter(adapter);
        showMoodTrends(moodEntries);
        // Back Button
        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }
    private void setupChart() {
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setGranularity(1f);
        lineChart.getAxisLeft().setGranularity(1f);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setEnabled(false);}


    private void showMoodTrends(List<MoodEntry> moodEntries) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < moodEntries.size(); i++) {
            entries.add(new Entry(i, moodToValue(moodEntries.get(i).getMood())));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Mood Trend");
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setColor(0xFF6200EE);
        dataSet.setCircleColor(0xFF6200EE);
        dataSet.setDrawValues(true); // show emoji/text
        //  Show emoji/text as label instead of number
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                int index = (int) entry.getX();
                if (index >= 0 && index < moodEntries.size()) {
                    return moodEntries.get(index).getMood();
                }
                return "";
            }
        });

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Custom MarkerView for tooltip
        MoodMarkerView markerView = new MoodMarkerView(this, R.layout.custom_marker_view, moodEntries);
        markerView.setChartView(lineChart);
        lineChart.setMarker(markerView);

        lineChart.invalidate();
    }

    private int moodToValue(String mood) {
        mood = mood.trim();

        switch (mood) {
            case "😊": return 5;   // Happy
            case "😐": return 3;   // Neutral
            case "😞": return 1;   // Sad
            case "happy": return 5;
            case "neutral": return 3;
            case "sad": return 1;
            default: return 2;     // Custom mood (unknown)
        }
    }

}
