package noorofgratitute.com;
public class DuaModel {
    private String duaTitle;
    private String duaText;
    private int imageResId;
    private int audioResId;
    private String duaDescription;
    // constructor for ayat with Image and Audio
    public DuaModel(String duaTitle,String duaDescription, String duaText, int imageResId, int audioResId) {
        this.duaTitle = duaTitle;
        this.duaText = duaText;
        this.imageResId = imageResId;
        this.duaDescription = duaDescription;
        this.audioResId = audioResId;}
    public String getDuaTitle() { return duaTitle; }
    public String getDuaText() { return duaText; }
    public int getAudioResId() { return audioResId; }
    public String getDuaDescription() { return duaDescription; }}
