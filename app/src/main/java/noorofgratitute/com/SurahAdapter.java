package noorofgratitute.com;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
public class SurahAdapter extends RecyclerView.Adapter<SurahAdapter.ViewHolder> {
    private Context context;
    private List<Surah> surahList;
    public SurahAdapter(Context context, List<Surah> surahList) {
        this.context = context;
        this.surahList = surahList; }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_surah, parent, false);
        return new ViewHolder(view); }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Surah surah = surahList.get(position);
        holder.surahName.setText(surah.getName());
        //loading image from URL using Picasso
        Picasso.get().load(surah.getImageUrl()).into(holder.surahImage);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SurahDetailActivity.class);
            intent.putExtra("surahName", surah.getName());
            intent.putExtra("pdfFile", surah.getPdfFile());
            intent.putExtra("audioFile", surah.getAudioFile());
            intent.putExtra("surahImage", surah.getImageUrl());
            context.startActivity(intent);
        }); }
    @Override
    public int getItemCount() {
        return surahList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView surahName;
        ImageView surahImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            surahName = itemView.findViewById(R.id.surahName);
            surahImage = itemView.findViewById(R.id.surahImage);
        } } }
