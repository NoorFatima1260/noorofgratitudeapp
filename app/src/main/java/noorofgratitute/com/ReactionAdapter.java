package noorofgratitute.com;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ReactionAdapter extends RecyclerView.Adapter<ReactionAdapter.ViewHolder> {
    private Context context;
    private List<ReactionDetail> reactionList;

    public ReactionAdapter(Context context, List<ReactionDetail> list) {
        this.context = context;
        this.reactionList = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reaction_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReactionDetail detail = reactionList.get(position);
        holder.tvUsername.setText(detail.getUsername());
        holder.tvTime.setText(detail.getTimestamp());
        holder.tvTime.setText(getRelativeTime(detail.getTimestamp()));
        String reaction = detail.getReactionType();
        Log.d("REACTION_DEBUG", "Reaction type: " + reaction);

        if (reaction != null) {
            switch (reaction.trim().toLowerCase()) {
                case "like":
                    holder.tvReaction.setText("👍 Like");
                    break;
                case "love":
                    holder.tvReaction.setText("❤️ Love");
                    break;
                case "haha":
                    holder.tvReaction.setText("😂 Haha");
                    break;
                case "wow":
                    holder.tvReaction.setText("😮 Wow");
                    break;
                case "sad":
                    holder.tvReaction.setText("😢 Sad");
                    break;
                case "angry":
                    holder.tvReaction.setText("😡 Angry");
                    break;
                default:
                    holder.tvReaction.setText(reaction);
            }
        } else {
            holder.tvReaction.setText("👍 Like");
        }

        if (detail.getProfileImageUrl() != null && !detail.getProfileImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(detail.getProfileImageUrl())
                    .placeholder(R.drawable.default_profile_image)
                    .circleCrop()
                    .into(holder.ivProfile);
        } else {
            holder.ivProfile.setImageResource(R.drawable.default_profile_image);
        }


    }
    @Override
    public int getItemCount() {
        return reactionList.size();
    }
    private String getRelativeTime(String isoTimestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

        try {
            java.util.Date past = sdf.parse(isoTimestamp);
            java.util.Date now = new java.util.Date();

            long seconds = (now.getTime() - past.getTime()) / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (seconds < 60) {
                return "Just now";
            } else if (minutes < 60) {
                return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
            } else if (hours < 24) {
                return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
            } else if (days == 1) {
                return "Yesterday";
            } else if (days < 7) {
                return days + " day" + (days > 1 ? "s" : "") + " ago";
            } else {
                java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault());
                return dateFormat.format(past);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvReaction, tvTime;
        ImageView ivProfile;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvReaction = itemView.findViewById(R.id.tvReaction);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivProfile = itemView.findViewById(R.id.ivPostImage);
        }
    }
}
