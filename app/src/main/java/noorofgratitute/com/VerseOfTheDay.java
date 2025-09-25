package noorofgratitute.com;
import com.google.gson.annotations.SerializedName;
public class VerseOfTheDay {
    private String reference;
    @SerializedName("arabic")
    private String arabic;
    @SerializedName("translation")
    private String translation;
    @SerializedName("audio_url")
    private String audioUrl;
    public String getReference() { return reference; }
    public String getArabic()   { return arabic; }
    public String getTranslation() { return translation; }
    public String getAudioUrl() { return audioUrl; }
}
