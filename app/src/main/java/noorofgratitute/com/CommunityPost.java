package noorofgratitute.com;
import com.google.gson.annotations.SerializedName;
import java.util.List;
public class CommunityPost {
    private boolean commentsExpanded = false;
    @SerializedName("username")
    private String username;
    @SerializedName("content")
    private String text;
    @SerializedName("id")
    private int id;
    public int getId() {
        return id;
    }
    @SerializedName("comment_details")
    private List<CommentDetail> commentDetailList;
    @SerializedName("file_url")
    private String fileUrl;
    public List<CommentDetail> getCommentDetailList() {
        return commentDetailList;
    }
    public void setCommentDetailList(List<CommentDetail> commentDetailList) {
        this.commentDetailList = commentDetailList;}
    public void setId(int id) {
        this.id = id;
    }
    public boolean isCommentsExpanded() {
        return commentsExpanded;
    }
    public void setCommentsExpanded(boolean expanded) {
        this.commentsExpanded = expanded;
    }
    @SerializedName("timestamp")
    private String timestamp;
    @SerializedName("profile_image_url")
    private String imageUrl;

    @SerializedName("current_user_reaction")
    private String currentUserReaction;
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getCurrentUserReaction() {
        return currentUserReaction;
    }
    public void setCurrentUserReaction(String currentUserReaction) {
        this.currentUserReaction = currentUserReaction;}
    public CommunityPost() {}
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getUsername() { return username; }
    public String getText() { return text; }
    public String getTimestamp() { return timestamp; }
    public String getImageUrl() { return imageUrl; }

    public void setUsername(String username) { this.username = username; }
    public void setText(String text) { this.text = text; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

  }
