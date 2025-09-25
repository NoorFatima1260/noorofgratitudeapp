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
public class fragment_gratitude_journal extends Fragment {
    Button btn_go_to_details,btn_go_to_progress_tracker;
    ImageButton btn_back;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gratitude_journal, container, false);
        btn_go_to_details = view.findViewById(R.id.btn_go_to_details);
        btn_go_to_progress_tracker = view.findViewById(R.id.btn_go_to_progress_tracker); // Initialize new button
        btn_back = view.findViewById(R.id.btn_back);
        btn_go_to_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), GratitudeDetailsActivity.class);
                startActivity(intent1);
            }});
        // go to GratitudeProgressTrackerActivity
        btn_go_to_progress_tracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getActivity(), gratitudeprogress_tracker.class);
                startActivity(intent2);
            }});
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }});
        return view;}}
