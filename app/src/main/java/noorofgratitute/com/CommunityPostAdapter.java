package noorofgratitute.com;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class CommunityPostAdapter extends RecyclerView.Adapter<CommunityPostAdapter.ViewHolder> {
    private List<CommunityPost> postList;
    private Context context;
    public CommunityPostAdapter(Context context, List<CommunityPost> postList) {
        this.context = context;
        this.postList = postList;}
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_community_post, parent, false);
        return new ViewHolder(view);}
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommunityPost post = postList.get(position);
        holder.tvUsername.setText(post.getUsername());
        holder.tvText.setText(post.getText());
        int commentCount = (post.getCommentDetailList() != null) ? post.getCommentDetailList().size() : 0;
        holder.tvCommentSummary.setText(commentCount + "");
        holder.tvCommentSummary.setOnClickListener(v -> {
            boolean isExpanded = post.isCommentsExpanded();
            post.setCommentsExpanded(!isExpanded);
            notifyItemChanged(position);

            CommentBottomSheet sheet = new CommentBottomSheet(position, post.getId(), (pos, commentText) -> {
                refreshComments(post.getId(), pos);
            });
            sheet.show(((AppCompatActivity) context).getSupportFragmentManager(), "CommentSheet");
        });
        loadReactionCount(post.getId(), holder.tvReactionSummary);
        holder.tvTimestamp.setText(getRelativeTime(post.getTimestamp()));
        holder.btnMore.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(holder.itemView.getContext(), holder.btnMore);
            popupMenu.inflate(R.menu.post_menue_edit_del);
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                int currentPosition = holder.getAdapterPosition();
                int postId = post.getId();

                if (itemId == R.id.post_edit) {
                    showEditDialog(postId, post.getText(), currentPosition);
                    return true;
                } else if (itemId == R.id.post_delete) {
                    showDeleteConfirmDialog(postId, currentPosition);
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });

        holder.btntotalReaction.setOnClickListener(v -> {
            ReactionBottomSheet sheet = new ReactionBottomSheet(post.getId());
            sheet.show(((AppCompatActivity) context).getSupportFragmentManager(), "ReactionSheet");});
        holder.mediaContainer.setBackground(null);
        holder.filePreviewImage.setVisibility(View.GONE);
        holder.videoPreview.setVisibility(View.GONE);
        holder.pdfIcon.setVisibility(View.GONE);
        holder.audioIcon.setVisibility(View.GONE);
        String fileUrl = post.getFileUrl();
        if (fileUrl != null && !fileUrl.isEmpty()) {
            if (!fileUrl.startsWith("http")) {
                fileUrl = "" + fileUrl;
                post.setFileUrl(fileUrl);}
            if (fileUrl.endsWith(".jpg") || fileUrl.endsWith(".jpeg") || fileUrl.endsWith(".png") || fileUrl.endsWith(".webp")) {
                holder.filePreviewImage.setVisibility(View.VISIBLE);
                Glide.with(context).load(fileUrl).into(holder.filePreviewImage);
                String finalFileUrl = fileUrl;
                holder.filePreviewImage.setOnClickListener(v -> {
                    Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                    dialog.setContentView(R.layout.dialog_fullscreen_image);
                    ImageView imageView = dialog.findViewById(R.id.fullscreenImage);
                    Glide.with(context).load(finalFileUrl).into(imageView);
                    imageView.setOnClickListener(view -> dialog.dismiss());
                    dialog.show();});
            } else if (fileUrl.endsWith(".mp4")) {
                holder.videoPreview.setVisibility(View.VISIBLE);
                holder.videoPreview.setVideoURI(Uri.parse(fileUrl));
                android.widget.MediaController mediaController = new android.widget.MediaController(context);
                mediaController.setAnchorView(holder.videoPreview);
                holder.videoPreview.setMediaController(mediaController);
                holder.videoPreview.setOnPreparedListener(mp -> {
                    mp.setVolume(1f, 1f);
                    holder.videoPreview.seekTo(1);
                    mediaController.setMediaPlayer(holder.videoPreview);
                    holder.mediaContainer.setBackground(null);});
                String finalFileUrl = fileUrl;
                holder.videoPreview.setOnClickListener(v -> {
                    try {
                        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                        dialog.setContentView(R.layout.dialog_fullscreen_video);
                        dialog.show();
                        VideoView videoView = dialog.findViewById(R.id.fullscreenVideo);
                        SeekBar seekBar = dialog.findViewById(R.id.fullscreenSeekBar);
                        ImageView btnPlayPause = dialog.findViewById(R.id.btnPlayPause);
                        FrameLayout container = dialog.findViewById(R.id.fullscreenContainer);
                        if (videoView == null || seekBar == null || btnPlayPause == null || container == null) {
                            Toast.makeText(context, "VideoView components missing", Toast.LENGTH_SHORT).show();
                            return;}
                        videoView.setVideoURI(Uri.parse(finalFileUrl));
                        videoView.requestFocus();
                        Handler handler = new Handler();
                        Runnable updateSeekBar = new Runnable() {
                            @Override
                            public void run() {
                                if (videoView.isPlaying()) {
                                    seekBar.setProgress(videoView.getCurrentPosition());
                                    handler.postDelayed(this, 500);
                                }}};
                        videoView.setOnPreparedListener(mp -> {
                            videoView.start();
                            btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                            seekBar.setMax(videoView.getDuration());
                            handler.postDelayed(updateSeekBar, 0);
                        });
                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                if (fromUser) {
                                    videoView.seekTo(progress);}}
                            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
                        });
                        btnPlayPause.setOnClickListener(view -> {
                            if (videoView.isPlaying()) {
                                videoView.pause();
                                btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                            } else {
                                videoView.start();
                                btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                            }});
                        //swipe down dismiss
                        container.setOnTouchListener(new View.OnTouchListener() {
                            float startY = 0;
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                switch (event.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        startY = event.getY();
                                        break;
                                    case MotionEvent.ACTION_UP:
                                        float endY = event.getY();
                                        if (startY - endY < -150) { // swipe down
                                            dialog.dismiss();}
                                        break;}
                                return false;}});
                        videoView.setOnCompletionListener(mp -> {
                            handler.removeCallbacks(updateSeekBar);
                            dialog.dismiss();});
                    } catch (Exception e) {
                        Toast.makeText(context, "Video error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();}});
            } else if (fileUrl.endsWith(".pdf")) {
                holder.pdfIcon.setVisibility(View.VISIBLE);
                String finalFileUrl = fileUrl;
                holder.pdfIcon.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(finalFileUrl), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    context.startActivity(intent);});
            } else if (fileUrl.endsWith(".mp3") || fileUrl.endsWith(".wav")) {
                holder.audioIcon.setVisibility(View.VISIBLE);
                String finalFileUrl = fileUrl;
                holder.audioIcon.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(finalFileUrl), "audio/*");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    context.startActivity(intent);
                });}}
        holder.btnComment.setOnClickListener(v -> {
            boolean isExpanded = post.isCommentsExpanded();
            post.setCommentsExpanded(!isExpanded);
            notifyItemChanged(position);

            CommentBottomSheet sheet = new CommentBottomSheet(position, post.getId(), (pos, commentText) -> {
                refreshComments(post.getId(), pos);
            });
            sheet.show(((AppCompatActivity) context).getSupportFragmentManager(), "CommentSheet");});
        if (post.isCommentsExpanded()) {
            List<CommentDetail> commentDetails = post.getCommentDetailList();
            if (commentDetails != null && !commentDetails.isEmpty()) {
                holder.rvComments.setVisibility(View.VISIBLE);
                CommentAdapter adapter = new CommentAdapter(context, commentDetails);
                holder.rvComments.setLayoutManager(new LinearLayoutManager(context));
                holder.rvComments.setAdapter(adapter);
            } else {
                holder.rvComments.setVisibility(View.GONE);}
        } else {
            holder.rvComments.setVisibility(View.GONE);}
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(post.getImageUrl())
                    .placeholder(R.drawable.default_profile_image)
                    .circleCrop()
                    .into(holder.ivPostImage);
        } else {
            holder.ivPostImage.setImageResource(R.drawable.default_profile_image);}
        String currentReaction = post.getCurrentUserReaction();
        if (currentReaction != null) {
            String emoji = getEmojiFromType(currentReaction);
            holder.btnReaction.setImageResource(getReactionIcon(currentReaction));
            holder.btnReaction.setContentDescription(emoji);
            holder.tvlike.setText(capitalizeReaction(currentReaction));
        } else {
            holder.btnReaction.setImageResource(R.drawable.ic_like);
            holder.btnReaction.setColorFilter(null);
            holder.tvlike.setText("Like");}
        holder.btnReaction.setOnTouchListener(new View.OnTouchListener() {
            private static final int LONG_PRESS_DURATION = 500;
            private boolean isPopupShown = false;
            private final Handler handler = new Handler();
            private final Runnable showPopupRunnable = () -> {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    CommunityPost currentPost = postList.get(currentPosition);
                    isPopupShown = true;
                    showReactionPopup(holder.btnReaction, currentPost.getId(), currentPosition);}};
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isPopupShown = false;
                        handler.postDelayed(showPopupRunnable, LONG_PRESS_DURATION);
                        return true;
                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacks(showPopupRunnable);
                        if (!isPopupShown) {
                            int currentPosition = holder.getAdapterPosition();
                            if (currentPosition != RecyclerView.NO_POSITION) {
                                CommunityPost currentPost = postList.get(currentPosition);
                                sendReaction(currentPost.getId(), "like", currentPosition);}}
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                        handler.removeCallbacks(showPopupRunnable);
                        return true;}
                return false;
            }});
        holder.btnShare.setOnClickListener(v -> {
            String shareText = post.getUsername() + " shared:\n\n" + post.getText();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            context.startActivity(Intent.createChooser(shareIntent, "Share via"));
        });}
    @Override
    public int getItemCount() {
        return postList.size();
    }
    private String getRelativeTime(String timestamp) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(timestamp);
            long time = (date != null) ? date.getTime() : System.currentTimeMillis();

            return DateUtils.getRelativeTimeSpanString(
                    time,
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE
            ).toString();

        } catch (Exception e) {
            e.printStackTrace();
            return timestamp;
        }
    }
    private void loadReactionCount(int postId, TextView tvReactionSummary) {
        TokenManager.getValidToken(context, new TokenManager.TokenCallback() {
            @Override
            public void onTokenReady(String jwt) {
                ApiServiceCommunity api = ApiClientCommunity.getClient().create(ApiServiceCommunity.class);
                Call<List<ReactionDetail>> call = api.getReactions("Bearer " + jwt, postId);

                call.enqueue(new Callback<List<ReactionDetail>>() {
                    @Override
                    public void onResponse(Call<List<ReactionDetail>> call, Response<List<ReactionDetail>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            int totalReactions = response.body().size();
                            tvReactionSummary.setText("" + totalReactions);
                        } else {
                            tvReactionSummary.setText("Reactions: 0");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ReactionDetail>> call, Throwable t) {
                        tvReactionSummary.setText("Reactions: 0");
                    }
                });
            }

            @Override public void onError(String errorMessage) {}
            @Override public void onFailure() {}
        });
    }

    private void showReactionPopup(View anchor, int postId, int position) {
        View popupView = LayoutInflater.from(context).inflate(R.layout.reaction_popup, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setElevation(10f);
        popupWindow.showAsDropDown(anchor, -50, -anchor.getHeight() - 60);
        int[] emojiIds = {
                R.id.emoji_like, R.id.emoji_love, R.id.emoji_haha,
                R.id.emoji_wow, R.id.emoji_sad, R.id.emoji_angry
        };
        String[] reactionTypes = {"like", "love", "haha", "wow", "sad", "angry"};
        for (int i = 0; i < emojiIds.length; i++) {
            TextView emoji = popupView.findViewById(emojiIds[i]);
            String reactionType = reactionTypes[i];
            emoji.setOnClickListener(view -> {
                popupWindow.dismiss();
                sendReaction(postId, reactionType, position);});}}
    private void refreshComments(int postId, int position) {
        TokenManager.getValidToken(context, new TokenManager.TokenCallback() {
            @Override
            public void onTokenReady(String jwt) {
                ApiServiceCommunity apiService = ApiClientCommunity.getClient().create(ApiServiceCommunity.class);
                Call<CommunityPost> call = apiService.getPostById("Bearer " + jwt, postId);
                call.enqueue(new Callback<CommunityPost>() {
                    @Override
                    public void onResponse(Call<CommunityPost> call, Response<CommunityPost> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            postList.set(position, response.body());
                            notifyItemChanged(position);}}
                    @Override
                    public void onFailure(Call<CommunityPost> call, Throwable t) {
                        Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                    }});}
            @Override
            public void onError(String errorMessage) {
                Toast.makeText(context, "Auth error: " + errorMessage, Toast.LENGTH_SHORT).show();}
            @Override
            public void onFailure() {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
            }});}
    private String capitalizeReaction(String reaction) {
        if (reaction == null || reaction.isEmpty()) return "";
        return reaction.substring(0, 1).toUpperCase() + reaction.substring(1).toLowerCase();}
    private void sendReaction(int postId, String reactionType, int position) {
        TokenManager.getValidToken(context, new TokenManager.TokenCallback() {
            @Override
            public void onTokenReady(String jwt) {
                ApiServiceCommunity api = ApiClientCommunity.getClient().create(ApiServiceCommunity.class);
                ReactionRequest request = new ReactionRequest(reactionType);
                api.sendReaction("Bearer " + jwt, postId, request).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(context, "Reacted with " + reactionType, Toast.LENGTH_SHORT).show();
                            postList.get(position).setCurrentUserReaction(reactionType);
                            notifyItemChanged(position);
                        } else {
                            Toast.makeText(context, "Reaction failed", Toast.LENGTH_SHORT).show();
                        }}
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                    }});}
            @Override
            public void onError(String errorMessage) {
                Toast.makeText(context, "Auth error: " + errorMessage, Toast.LENGTH_SHORT).show();}
            @Override
            public void onFailure() {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();}});}
    private int getReactionIcon(String type) {
        switch (type.toLowerCase()) {
            case "like": return R.drawable.like_filled;
            case "love": return R.drawable.ic_love;
            case "haha": return R.drawable.ic_haha;
            case "wow": return R.drawable.ic_wow;
            case "sad": return R.drawable.ic_sad;
            case "angry": return R.drawable.ic_angry;
            default: return R.drawable.ic_like;}}
    private String getEmojiFromType(String type) {
        switch (type.toLowerCase()) {
            case "like": return "👍";
            case "love": return "❤️";
            case "haha": return "😂";
            case "wow": return "😮";
            case "sad": return "😢";
            case "angry": return "😡";
            default: return "👍";}}
    private void showEditDialog(int postId, String currentText, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Post");

        final EditText input = new EditText(context);
        input.setText(currentText);
        input.setSelection(currentText.length());
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String updatedText = input.getText().toString();
            updatePost(postId, updatedText, position);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void deletePost(int postId, int position) {
        TokenManager.getValidToken(context, new TokenManager.TokenCallback() {
            @Override
            public void onTokenReady(String jwt) {
                ApiServiceCommunity api = ApiClientCommunity.getClient().create(ApiServiceCommunity.class);
                Call<ResponseBody> call = api.deletePost("Bearer " + jwt, postId);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            postList.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(context, "Auth error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(context, "Token fetch failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePost(int postId, String newText, int position) {
        TokenManager.getValidToken(context, new TokenManager.TokenCallback() {
            @Override
            public void onTokenReady(String jwt) {
                ApiServiceCommunity api = ApiClientCommunity.getClient().create(ApiServiceCommunity.class);
                PostEditRequest request = new PostEditRequest(newText, "public");

                api.editPost("Bearer " + jwt, postId, request).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            postList.get(position).setText(newText);
                            notifyItemChanged(position);
                            Toast.makeText(context, "Post updated", Toast.LENGTH_SHORT).show();
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

            @Override public void onError(String errorMessage) {}
            @Override public void onFailure() {}
        });
    }
    private void showDeleteConfirmDialog(int postId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Post");
        builder.setMessage("Are you sure you want to delete this post?");
        builder.setPositiveButton("Yes", (dialog, which) -> deletePost(postId, position));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvText, tvTimestamp, tvComments, tvReactionSummary, tvlike,tvCommentSummary;
        ImageView ivPostImage, btnReaction, btnComment, btnShare, btntotalReaction;
        RecyclerView rvComments;
        ImageView filePreviewImage;
        VideoView videoPreview;
        ImageView pdfIcon,btnMore;
        ImageView audioIcon;
        LinearLayout reactionContainer;
        FrameLayout mediaContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvText = itemView.findViewById(R.id.tvPostText);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvReactionSummary = itemView.findViewById(R.id.tvReactionSummary);
            tvComments = itemView.findViewById(R.id.tvComments);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            btnReaction = itemView.findViewById(R.id.btnReaction);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnShare = itemView.findViewById(R.id.btnShare);
            rvComments = itemView.findViewById(R.id.rvComments);
            btntotalReaction = itemView.findViewById(R.id.btntotalReaction);
            tvlike = itemView.findViewById(R.id.tvlike);
            filePreviewImage = itemView.findViewById(R.id.filePreviewImage);
            videoPreview = itemView.findViewById(R.id.videoPreview);
            pdfIcon = itemView.findViewById(R.id.pdfIcon);
            audioIcon = itemView.findViewById(R.id.audioIcon);
            reactionContainer = itemView.findViewById(R.id.reactionContainer);
            mediaContainer = itemView.findViewById(R.id.media_container);
            btnMore = itemView.findViewById(R.id.btnMore);
            tvCommentSummary = itemView.findViewById(R.id.txtcommentsSummary);
        }}}