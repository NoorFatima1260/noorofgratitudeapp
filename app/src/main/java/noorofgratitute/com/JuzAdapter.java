package noorofgratitute.com;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;
public class JuzAdapter extends RecyclerView.Adapter<JuzAdapter.ViewHolder> {
    private Context context;
    private List<Juz> juzList;
    public JuzAdapter(Context context, List<Juz> juzList) {
        this.context = context;
        this.juzList = juzList;}
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_juz, parent, false);
        return new ViewHolder(view);}
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Juz juz = juzList.get(position);
        holder.juzName.setText(juz.getName());
        //using Picasso to load image from url
        Picasso.get().load(juz.getImageUrl()).into(holder.juzImage);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, JuzDetailActivity.class);
            intent.putExtra("pdfFile", juz.getPdfFile());
            intent.putExtra("audioFile", juz.getAudioFile());
            intent.putExtra("juzName", juz.getName());
            context.startActivity(intent);
        });}
    @Override
    public int getItemCount() {
        return juzList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView juzName;
        ImageView juzImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            juzName = itemView.findViewById(R.id.juzName);
            juzImage = itemView.findViewById(R.id.juzImage);
        }}}
