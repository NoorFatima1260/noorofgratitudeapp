package noorofgratitute.com;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
public class FragmentOfMoodJournal extends Fragment {
    private Button btnDailyMoodLogging, btnMoodReminders;
    private ImageButton btnBack;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //mood journal fragment
        View view = inflater.inflate(R.layout.fragment_of_mood_journal, container, false);
        btnDailyMoodLogging = view.findViewById(R.id.btn_daily_mood_logging);
        btnMoodReminders = view.findViewById(R.id.btn_mood_reminders);
        btnBack = view.findViewById(R.id.btn_back);
        btnDailyMoodLogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DailyMoodLoggingActivity.class);
                startActivity(intent);
            }});
        btnMoodReminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the MoodHistoryActivity
                Intent intent = new Intent(getActivity(), MoodHistoryActivity.class);
                startActivity(intent);
            }});
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go back to the previous fragment
                requireActivity().getSupportFragmentManager().popBackStack();}});
        return view;}}
