package noorofgratitute.com;
import com.google.gson.annotations.SerializedName;
public class ReactionDetail {
    @SerializedName("username")
    private String username;
    @SerializedName("profile_image_url")
    private String profileImageUrl;
    @SerializedName("timestamp")
    private String timestamp;
    @SerializedName("reaction_type")
    private String reactionType;

    public String getUsername() { return username; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public String getReactionType() { return reactionType; }
    public String getTimestamp() { return timestamp; }

}
