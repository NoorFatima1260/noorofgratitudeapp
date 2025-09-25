package noorofgratitute.com;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
public class DuaAdapter extends RecyclerView.Adapter<DuaAdapter.ViewHolder> {
    private Context context;
    private ArrayList<DuaModel> duaList;
    private ArrayList<DuaModel> duaListFull ;
    private Set<String> bookmarkedDuas;
    private SharedPreferences sharedPreferences;
    private static final String BOOKMARKS_KEY = "bookmarked_duas";
    private MediaPlayer mediaPlayer;
    private int currentPlayingPosition = -1;
    public DuaAdapter(Context context, ArrayList<DuaModel> duaList) {
        this.context = context;
        this.duaList = new ArrayList<>(duaList);
        this.duaListFull = new ArrayList<>(duaList);
        this.sharedPreferences = context.getSharedPreferences("DuaPrefs", Context.MODE_PRIVATE);
        this.bookmarkedDuas = new HashSet<>(sharedPreferences.getStringSet(BOOKMARKS_KEY, new HashSet<>()));}
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dua, parent, false);
        return new ViewHolder(view);}
    //search filter function
    public void filter(String query) {
        query = query.toLowerCase().trim();
        duaList.clear();
        if (query.isEmpty()) {
            duaList.addAll(duaListFull);
        } else {
            for (DuaModel dua : duaListFull) {
                if (dua.getDuaTitle().toLowerCase().contains(query) ||
                        dua.getDuaText().toLowerCase().contains(query)) {
                    duaList.add(dua);}}}
        notifyDataSetChanged();}
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DuaModel dua = duaList.get(position);
        holder.duaTitle.setText(dua.getDuaTitle());
        holder.duaText.setText(dua.getDuaText());
        holder.duaDescription.setText(dua.getDuaDescription());
        //bookmark
        holder.bookmarkButton.setImageResource(
                bookmarkedDuas.contains(dua.getDuaTitle()) ? R.drawable.bookmark_filled: R.drawable.bookmark_icon);
        holder.bookmarkButton.setOnClickListener(v -> {
            boolean isBookmarked = !bookmarkedDuas.contains(dua.getDuaTitle());
            if (isBookmarked) {
                bookmarkedDuas.add(dua.getDuaTitle());
            } else {
                bookmarkedDuas.remove(dua.getDuaTitle());}
            sharedPreferences.edit().putStringSet(BOOKMARKS_KEY, bookmarkedDuas).apply();
            holder.bookmarkButton.setImageResource(isBookmarked ? R.drawable.bookmark_filled : R.drawable.bookmark_icon);
        });
        //play/pause audio
        holder.playButton.setOnClickListener(v -> {
            if (dua.getAudioResId() != 0) {
                if (mediaPlayer != null && currentPlayingPosition == position) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        holder.playButton.setImageResource(R.drawable.play);
                    } else {
                        mediaPlayer.start();
                        holder.playButton.setImageResource(R.drawable.pause_button);}
                } else {
                    playAudio(position, dua.getAudioResId(), holder);
                }}});}
    private void playAudio(int position, int audioResId, ViewHolder holder) {
        if (mediaPlayer != null) {
            mediaPlayer.release();}
        mediaPlayer = MediaPlayer.create(context, audioResId);
        mediaPlayer.start();
        currentPlayingPosition = position;
        holder.playButton.setImageResource(R.drawable.pause_button);
        mediaPlayer.setOnCompletionListener(mp -> {
            holder.playButton.setImageResource(R.drawable.play);});}
    @Override
    public int getItemCount() {
        return duaList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView duaTitle, duaText,duaDescription;
        ImageButton bookmarkButton, playButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            duaTitle = itemView.findViewById(R.id.duaTitle);
            duaText = itemView.findViewById(R.id.duaText);
            bookmarkButton = itemView.findViewById(R.id.bookmarkButton);
            playButton = itemView.findViewById(R.id.playButton);
            duaDescription = itemView.findViewById(R.id.duaDescription);}}}
