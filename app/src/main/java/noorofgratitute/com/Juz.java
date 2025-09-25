package noorofgratitute.com;
import com.google.gson.annotations.SerializedName;
public class Juz {
    private String name;
    private int number;

    @SerializedName("number_image_url")
    private String imageUrl;
    @SerializedName("pdf_url")
    private String pdfFile;
    @SerializedName("audio_url")
    private String audioFile;


    public Juz(String name, String imageUrl, String pdfFile, String audioFile) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.pdfFile = pdfFile;
        this.audioFile = audioFile;}

    public Juz(int number, String name) {
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public String getPdfFile() {
        return pdfFile;
    }
    public String getAudioFile() {
        return audioFile;
    }
    // Add this getter



    // ✅ Constructor
    public Juz(int number, String name, String pdfFile, String audioFile) {
        this.number = number;
        this.name = name;
    }

    // ✅ Getters
    public int getNumber() { return number; }
    public String getName() { return name; }
}
