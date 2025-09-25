package noorofgratitute.com;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class MoodAdapter extends RecyclerView.Adapter<MoodAdapter.MoodViewHolder> {
    private List<MoodEntry> moodList;
    @NonNull
    @Override
    public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mood_history, parent, false);
        return new MoodViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MoodViewHolder holder, int position) {
        MoodEntry moodEntry = moodList.get(position);
        holder.textViewMood.setText(moodEntry.getMood());
        holder.textViewDate.setText(moodEntry.getDate());
    }
    @Override
    public int getItemCount() {
        return moodList.size();
    }
    public static class MoodViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMood, textViewDate;
        public MoodViewHolder(View itemView) {
            super(itemView);
            textViewMood = itemView.findViewById(R.id.moodTextView);
            textViewDate = itemView.findViewById(R.id.dateTextView);
        }}}
