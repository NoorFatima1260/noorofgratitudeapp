package noorofgratitute.com;
import android.content.Context;
import android.widget.TextView;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.MPPointF;
import java.util.List;

public class MoodMarkerView extends MarkerView {
    private TextView tvContent;
    private List<MoodEntry> moodEntries;

    public MoodMarkerView(Context context, int layoutResource, List<MoodEntry> moodEntries) {
        super(context, layoutResource);
        this.moodEntries = moodEntries;
        tvContent = findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int index = (int) e.getX();
        if (index >= 0 && index < moodEntries.size()) {
            MoodEntry moodEntry = moodEntries.get(index);
            tvContent.setText("📅 " + moodEntry.getDate() + "\nMood: " + moodEntry.getMood());
        } else {
            tvContent.setText("No data");
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2f), -getHeight());
    }
}
