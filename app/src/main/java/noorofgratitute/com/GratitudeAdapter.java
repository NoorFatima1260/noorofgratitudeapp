package noorofgratitute.com;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class GratitudeAdapter extends RecyclerView.Adapter<GratitudeAdapter.ViewHolder> {
    private final List<String> gratitudeList;
    public GratitudeAdapter(List<String> gratitudeList) {
        this.gratitudeList = gratitudeList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gratitude, parent, false);
        return new ViewHolder(view);}
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String gratitude = gratitudeList.get(position);
        holder.tvGratitudeEntry.setText(gratitude);}
    @Override
    public int getItemCount() {
        return gratitudeList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGratitudeEntry;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGratitudeEntry = itemView.findViewById(R.id.tv_gratitude_entry);}}}
