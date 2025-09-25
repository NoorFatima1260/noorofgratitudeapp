package noorofgratitute.com;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import android.widget.PopupMenu;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.text.format.DateUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {
    private final List<CommentDetail> replyList;
    private final Context context;
    private final List<Boolean> nestedRepliesVisibility = new ArrayList<>();

    public ReplyAdapter(Context context, List<CommentDetail> replyList) {
        this.context = context;
        this.replyList = replyList;
        for (CommentDetail reply : replyList) {
            nestedRepliesVisibility.add(true);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reply, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentDetail reply = replyList.get(position);

        holder.tvReplyUsername.setText(reply.getUsername());
        holder.tvReplyContent.setText(reply.getContent());
        holder.tvReplyTimestamp.setText(getRelativeTime(reply.getTimestamp()));

        if (reply.getProfileImageUrl() != null && !reply.getProfileImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(reply.getProfileImageUrl())
                    .placeholder(R.drawable.default_profile_image)
                    .into(holder.ivReplyProfile);
        } else {
            holder.ivReplyProfile.setImageResource(R.drawable.default_profile_image);
        }

        String userReaction = reply.getUserReaction(); // <-- You must have this field from backend
        if ("like".equals(userReaction)) {
            holder.btnReplyLike.setImageResource(R.drawable.like_filled);     // green filled
            holder.btnReplyDislike.setImageResource(R.drawable.dislike);
        } else if ("dislike".equals(userReaction)) {
            holder.btnReplyLike.setImageResource(R.drawable.ic_like);
            holder.btnReplyDislike.setImageResource(R.drawable.dislike_illed); // green filled
        } else {
            holder.btnReplyLike.setImageResource(R.drawable.ic_like);
            holder.btnReplyDislike.setImageResource(R.drawable.dislike);
        }
        // 🔄 Reaction Click Listeners
        holder.btnReplyLike.setOnClickListener(v -> sendCommentReaction(reply.getId(), "like", position));
        holder.btnReplyDislike.setOnClickListener(v -> sendCommentReaction(reply.getId(), "dislike", position));
        holder.btnNestedReply.setOnClickListener(v -> showReplyInputDialog(reply.getId(), position));

        // 🔽 Nested Replies
        List<CommentDetail> nestedReplies = reply.getReplies();
        if (nestedReplies != null && !nestedReplies.isEmpty()) {
            holder.tvToggleNestedReplies.setVisibility(View.VISIBLE);
            boolean isVisible = nestedRepliesVisibility.get(position);
            holder.rvNestedReplies.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            holder.tvToggleNestedReplies.setText(isVisible ? "Hide Replies" : "View Replies");

            if (isVisible) {
                ReplyAdapter nestedAdapter = new ReplyAdapter(context, nestedReplies);
                holder.rvNestedReplies.setLayoutManager(new LinearLayoutManager(context));
                holder.rvNestedReplies.setAdapter(nestedAdapter);
            }

            holder.tvToggleNestedReplies.setOnClickListener(v -> {
                boolean newState = !nestedRepliesVisibility.get(position);
                nestedRepliesVisibility.set(position, newState);
                notifyItemChanged(position);
            });
        } else {
            holder.rvNestedReplies.setVisibility(View.GONE);
            holder.tvToggleNestedReplies.setVisibility(View.GONE);
        }
        holder.btnMore.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.btnMore);
            popup.inflate(R.menu.comment_options_menu); // same menu used in CommentAdapter
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_edit) {
                    showEditDialog(reply.getId(), reply.getContent(), position);
                    return true;
                } else if (item.getItemId() == R.id.action_delete) {
                    confirmDeleteComment(reply.getId(), position);
                    return true;
                }
                return false;
            });
            popup.show();
        });

    }
    private void showEditDialog(int commentId, String currentText, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Reply");
        final EditText input = new EditText(context);
        input.setText(currentText);
        builder.setView(input);
        builder.setPositiveButton("Update", (dialog, which) -> {
            String updatedText = input.getText().toString().trim();
            if (!updatedText.isEmpty()) {
                updateComment(commentId, updatedText, position);
            } else {
                Toast.makeText(context, "Reply cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateComment(int commentId, String newText, int position) {
        TokenManager.getValidToken(context, new TokenManager.TokenCallback() {
            @Override
            public void onTokenReady(String jwt) {
                ApiServiceCommunity api = ApiClientCommunity.getClient().create(ApiServiceCommunity.class);
                CommentRequest request = new CommentRequest(newText);
                api.editComment("Bearer " + jwt, commentId, request).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            replyList.get(position).setContent(newText);
                            notifyItemChanged(position);
                            Toast.makeText(context, "Reply updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override public void onFailure() {}
            @Override public void onError(String message) {}
        });
    }
    private void confirmDeleteComment(int commentId, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Reply")
                .setMessage("Are you sure you want to delete this reply?")
                .setPositiveButton("Yes", (dialog, which) -> deleteComment(commentId, position))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteComment(int commentId, int position) {
        TokenManager.getValidToken(context, new TokenManager.TokenCallback() {
            @Override
            public void onTokenReady(String jwt) {
                ApiServiceCommunity api = ApiClientCommunity.getClient().create(ApiServiceCommunity.class);
                api.deleteComment("Bearer " + jwt, commentId).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            replyList.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Reply deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to delete reply", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override public void onFailure() {}
            @Override public void onError(String message) {}
        });
    }


    @Override
    public int getItemCount() {
        return replyList.size();
    }
    private String getRelativeTime(String isoTimestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        try {
            Date past = sdf.parse(isoTimestamp);
            Date now = new Date();

            long seconds = (now.getTime() - past.getTime()) / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (seconds < 60) {
                return "Just now";
            } else if (minutes < 60) {
                return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
            } else if (hours < 24) {
                return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
            } else if (days == 1) {
                return "Yesterday";
            } else if (days < 7) {
                return days + " day" + (days == 1 ? "" : "s") + " ago";
            } else {
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                return outputFormat.format(past);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "Just now";
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivReplyProfile, btnReplyLike, btnReplyDislike;
        TextView tvReplyUsername, tvReplyTimestamp, tvReplyContent, btnNestedReply, tvToggleNestedReplies;
        RecyclerView rvNestedReplies;
        ImageView btnMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivReplyProfile = itemView.findViewById(R.id.ivReplyProfile);
            tvReplyUsername = itemView.findViewById(R.id.tvReplyUsername);
            tvReplyTimestamp = itemView.findViewById(R.id.tvReplyTimestamp);
            tvReplyContent = itemView.findViewById(R.id.tvReplyContent);
            btnReplyLike = itemView.findViewById(R.id.btnReplyLike);
            btnReplyDislike = itemView.findViewById(R.id.btnReplyDislike);
            btnNestedReply = itemView.findViewById(R.id.btnnestedReply);
            rvNestedReplies = itemView.findViewById(R.id.rvNestedReplies);
            tvToggleNestedReplies = itemView.findViewById(R.id.tvToggleNestedReplies);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }

    private void showReplyInputDialog(int parentCommentId, int position) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("Reply to Reply");
        final EditText input = new EditText(context);
        input.setHint("Type your reply...");
        builder.setView(input);

        builder.setPositiveButton("Send", (dialog, which) -> {
            String replyText = input.getText().toString().trim();
            if (!replyText.isEmpty()) {
                sendReplyToComment(parentCommentId, replyText, position);
            } else {
                Toast.makeText(context, "Reply cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void sendReplyToComment(int parentCommentId, String replyText, int position) {
        int postId = replyList.get(position).getPostId();

        TokenManager.getValidToken(context, new TokenManager.TokenCallback() {
            @Override
            public void onTokenReady(String jwt) {
                ApiServiceCommunity api = ApiClientCommunity.getClient().create(ApiServiceCommunity.class);
                CommentRequest request = new CommentRequest(replyText);
                api.replyToComment("Bearer " + jwt, postId, parentCommentId, request).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(context, "Reply sent", Toast.LENGTH_SHORT).show();
                            notifyItemChanged(position);
                        } else {
                            Toast.makeText(context, "Failed to reply", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure() {
                Toast.makeText(context, "Token failure", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(context, "Token error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendCommentReaction(int commentId, String reactionType, int position) {
        TokenManager.getValidToken(context, new TokenManager.TokenCallback() {
            @Override
            public void onTokenReady(String jwt) {
                ApiServiceCommunity api = ApiClientCommunity.getClient().create(ApiServiceCommunity.class);
                ReactionRequest request = new ReactionRequest(reactionType);
                api.toggleCommentReaction("Bearer " + jwt, commentId, request).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(context, "Reacted with " + reactionType, Toast.LENGTH_SHORT).show();
                            replyList.get(position).setUserReaction(reactionType); // 🟢 Update Reaction
                            notifyItemChanged(position);
                        } else {
                            Toast.makeText(context, "Failed to react", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure() {
                Toast.makeText(context, "Failed to get token", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(context, "Token error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
