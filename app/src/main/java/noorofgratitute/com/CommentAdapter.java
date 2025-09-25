package noorofgratitute.com;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private final List<CommentDetail> commentList;
    private final Context context;
    public CommentAdapter(Context context, List<CommentDetail> commentList) {
        this.context = context;
        this.commentList = commentList;}
    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false); // Make sure this matches your XML layout
        return new ViewHolder(view);}

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        CommentDetail comment = commentList.get(position);
        holder.tvUsername.setText(comment.getUsername());
        holder.tvCommentText.setText(comment.getContent());
        holder.tvTimestamp.setText(getRelativeTime(comment.getTimestamp()));
        holder.btnnestedReply.setVisibility(View.VISIBLE);
        holder.btnnestedReply.setOnClickListener(v -> {
            showReplyInputDialog(comment.getId(), position);
        });
        List<CommentDetail> replies = comment.getReplies();
        holder.rvReplies.setLayoutManager(new LinearLayoutManager(context));
        holder.rvReplies.setAdapter(new ReplyAdapter(context, replies != null ? replies : new ArrayList<>()));
        holder.rvReplies.setVisibility(View.GONE);
        if (replies != null && !replies.isEmpty()) {
            //show toggle
            holder.tvToggleReplies.setVisibility(View.VISIBLE);
            holder.tvToggleReplies.setText("Hide Replies");
            holder.tvToggleReplies.setOnClickListener(v -> {
                if (holder.rvReplies.getVisibility() == View.VISIBLE) {
                    holder.rvReplies.setVisibility(View.GONE);
                    holder.tvToggleReplies.setText("View Replies");
                } else {
                    holder.rvReplies.setVisibility(View.VISIBLE);
                    holder.tvToggleReplies.setText("Hide Replies");
                }});
        } else {
            holder.tvToggleReplies.setVisibility(View.GONE);  }
        //profile image
        if (comment.getProfileImageUrl() != null && !comment.getProfileImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(comment.getProfileImageUrl())
                    .placeholder(R.drawable.default_profile_image)
                    .into(holder.ivProfileImage);
        } else {
            holder.ivProfileImage.setImageResource(R.drawable.default_profile_image);  }
        //reactions
        String userReaction = comment.getUserReaction();
        if ("like".equals(userReaction)) {
            holder.btnCommentLike.setImageResource(R.drawable.like_filled);
            holder.btnCommentDislike.setImageResource(R.drawable.dislike);
        } else if ("dislike".equals(userReaction)) {
            holder.btnCommentLike.setImageResource(R.drawable.ic_like);
            holder.btnCommentDislike.setImageResource(R.drawable.dislike_illed);
        } else {
            holder.btnCommentLike.setImageResource(R.drawable.ic_like);
            holder.btnCommentDislike.setImageResource(R.drawable.dislike);
        }
        //reaction listeners
        holder.btnCommentLike.setOnClickListener(v -> {
            sendCommentReaction(comment.getId(), "like", position);
        });
        holder.btnCommentDislike.setOnClickListener(v -> {
            sendCommentReaction(comment.getId(), "dislike", position);
        });

        holder.btnMore.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.btnMore);
            popup.inflate(R.menu.comment_options_menu); // create this XML below
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_edit) {
                    showEditDialog(comment.getId(), comment.getContent(), position);
                    return true;
                } else if (item.getItemId() == R.id.action_delete) {
                    confirmDeleteComment(comment.getId(), position);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }
    private void showReplyInputDialog(int parentCommentId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Reply to Comment");
        final EditText input = new EditText(context);
        input.setHint("Type your reply...");
        builder.setView(input);
        builder.setPositiveButton("Send", (dialog, which) -> {
            String replyText = input.getText().toString().trim();
            if (!replyText.isEmpty()) {
                sendReplyToComment(parentCommentId, replyText, position);
            } else {
                Toast.makeText(context, "Reply cannot be empty", Toast.LENGTH_SHORT).show();
            } });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();  }
    private void showEditDialog(int commentId, String currentText, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Comment");
        final EditText input = new EditText(context);
        input.setText(currentText);
        builder.setView(input);
        builder.setPositiveButton("Update", (dialog, which) -> {
            String updatedText = input.getText().toString().trim();
            if (!updatedText.isEmpty()) {
                updateComment(commentId, updatedText, position);
            } else {
                Toast.makeText(context, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
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
                            commentList.get(position).setContent(newText);
                            notifyItemChanged(position);
                            Toast.makeText(context, "Comment updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override public void onFailure() {}
            @Override public void onError(String message) {}
        });
    }

    private void sendReplyToComment(int parentCommentId, String replyText, int position) {
        int postId = commentList.get(position).getPostId();
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
                        } }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                    } }); }
            @Override
            public void onFailure() {
                Toast.makeText(context, "Token failure", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(String message) {
                Toast.makeText(context, "Token error: " + message, Toast.LENGTH_SHORT).show();
            } });}
    private void confirmDeleteComment(int commentId, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Comment")
                .setMessage("Are you sure you want to delete this comment?")
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
                            commentList.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, "Token error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(context, "Token error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size(); }
    private String getRelativeTime(String isoTimestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

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
                return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
            } else if (hours < 24) {
                return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
            } else if (days == 1) {
                return "Yesterday";
            } else if (days < 7) {
                return days + " day" + (days > 1 ? "s" : "") + " ago";
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
                return dateFormat.format(past);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
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
                            commentList.get(position).setUserReaction(reactionType);
                            notifyItemChanged(position);
                            Toast.makeText(context, "Reacted to comment with " + reactionType, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(context, "Failed to react", Toast.LENGTH_SHORT).show();
                        } }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                    }}); }
            @Override
            public void onFailure() {
                Toast.makeText(context, "Failed to get token", Toast.LENGTH_SHORT).show();}
            @Override
            public void onError(String message) {
                Toast.makeText(context, "Token error: " + message, Toast.LENGTH_SHORT).show();
            }});  }
        public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfileImage , btnCommentLike, btnCommentDislike;
        TextView tvUsername, tvCommentText, tvTimestamp,btnnestedReply,tvToggleReplies;
        RecyclerView rvReplies;
        ImageView btnMore;

            public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCommentText = itemView.findViewById(R.id.tvCommentText);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            btnCommentLike = itemView.findViewById(R.id.btnCommentLike);
            btnCommentDislike = itemView.findViewById(R.id.btnCommentDislike);
            btnnestedReply = itemView.findViewById(R.id.btnnestedReply);
            rvReplies = itemView.findViewById(R.id.rvReplies);
            tvToggleReplies = itemView.findViewById(R.id.tvToggleReplies);
            btnMore = itemView.findViewById(R.id.btnMore);
            }}}