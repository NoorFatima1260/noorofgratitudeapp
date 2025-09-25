package noorofgratitute.com;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class MoodHistoryAdapter extends RecyclerView.Adapter<MoodHistoryAdapter.ViewHolder> {
    private List<MoodEntry> moodHistoryList;
    public MoodHistoryAdapter(List<MoodEntry> moodHistoryList) {
        this.moodHistoryList = moodHistoryList; }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mood_history, parent, false);
        return new ViewHolder(view); }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MoodEntry moodEntry = moodHistoryList.get(position);
        holder.moodTextView.setText(moodEntry.getMood());
        holder.dateTextView.setText(moodEntry.getDate()); }
    @Override
    public int getItemCount() {
        return moodHistoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView moodTextView, dateTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            moodTextView = itemView.findViewById(R.id.moodTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }}}
