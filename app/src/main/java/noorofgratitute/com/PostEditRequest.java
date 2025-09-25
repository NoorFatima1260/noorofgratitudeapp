package noorofgratitute.com;
public class PostEditRequest {
    private String content;
    private String privacy;
    public PostEditRequest(String content, String privacy) {
        this.content = content;
        this.privacy = privacy;
    }

    public String getContent() {
        return content;
    }
}
