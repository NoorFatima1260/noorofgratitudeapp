package noorofgratitute.com;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommentDetail {
    private int id;
    private String content;
    private String timestamp;
    private String username;
    private String profile_image_url;
    private Integer parent;
    @SerializedName("post_id")
    private int postId;
    private List<CommentDetail> replies;
    public int getPostId() {
        return postId;
    }
    public int getId() {
        return id;
    }
    public String getContent() {
        return content;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public String getUsername() {
        return username;
    }
    public String getProfileImageUrl() {
        return profile_image_url;
    }
    public Integer getParent() {
        return parent;}
    public List<CommentDetail> getReplies() {
        return replies;
    }
    private String userReaction; // "like", "dislike", or null

    public String getUserReaction() {
        return userReaction;
    }

    public void setUserReaction(String userReaction) {
        this.userReaction = userReaction;
    }
    public void setContent(String content) {
        this.content = content;
    }

}
