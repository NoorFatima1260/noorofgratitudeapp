package noorofgratitute.com;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.button.MaterialButton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
public class JuzDetailActivity extends AppCompatActivity {
    private static final int MIN_JUZ = 1;
    private static final int MAX_JUZ = 30;
    private PDFView pdfView;
    private ImageButton playPauseButton, bookmarkButton, btnBack;
    private MaterialButton btnPrevious, btnNext;
    private SeekBar audioSeekBar;
    private TextView juzNameText;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private static final String[] JUZ_NAMES = {
            "Alif Laam Meem",       // Juz 1
            "Sayaqool",             // Juz 2
            "Tilka Ar-Rusul",       // Juz 3
            "Lan Tanaaloo",         // Juz 4
            "Wal Mohsanat",         // Juz 5
            "La Yuhibbullah",       // Juz 6
            "Wa Iza Samiu",         // Juz 7
            "Wa Lau Annana",        // Juz 8
            "Qad Aflaha",           // Juz 9
            "Wa A’lamu",            // Juz 10
            "Ya Ayyuha Al-Ladhina", // Juz 11
            "Wa Mamin Da’abbah",    // Juz 12
            "Wa Ma Ubrioo",         // Juz 13
            "Rubama",               // Juz 14
            "Subhanalladhi",        // Juz 15
            "Qal Alam",             // Juz 16
            "Iqtarabat",            // Juz 17
            "Qadd Aflaha",          // Juz 18
            "Wa Qalalladhina",      // Juz 19
            "A’man Khalaqa",        // Juz 20
            "Utlu Ma Oohiya",       // Juz 21
            "Wa Manyaqnut",         // Juz 22
            "Wa Mali",              // Juz 23
            "Faman Azlamu",         // Juz 24
            "Elahe Yuruddu",        // Juz 25
            "Ha Meem",              // Juz 26
            "Qala Fama Khatbukum",  // Juz 27
            "Qadd Sami Allah",      // Juz 28
            "Tabarakalladhi",       // Juz 29
            "Amma Yatasa'aloon"     // Juz 30
    };

    private String pdfFile, audioFile;
    private boolean isPlaying = false;
    private boolean isBookmarked = false;
    private SharedPreferences sharedPreferences;
    private static final String BASE_URL = "http://192.168.0.105:8000/media/juzs/";
    private static final String TAG = "JuzDetailActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juz_detail);
        // View bindings
        pdfView = findViewById(R.id.pdfView);
        playPauseButton = findViewById(R.id.playPauseButton);
        bookmarkButton = findViewById(R.id.bookmarkButton);
        btnBack = findViewById(R.id.btnBack);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        audioSeekBar = findViewById(R.id.audioSeekBar);
        juzNameText = findViewById(R.id.JuzName);
        //get intent data
        pdfFile = getIntent().getStringExtra("pdfFile");
        audioFile = getIntent().getStringExtra("audioFile");
        String juzName = getIntent().getStringExtra("juzName");
        juzNameText.setText(juzName);
        sharedPreferences = getSharedPreferences("Bookmarks", MODE_PRIVATE);
        isBookmarked = sharedPreferences.getBoolean(pdfFile, false);
        updateBookmarkIcon();
        //load media
        loadPdfFromUrl(pdfFile);
        setupAudio(audioFile);
        int curr = extractJuzNumber(pdfFile);
        btnPrevious.setEnabled(curr > MIN_JUZ);
        btnNext.setEnabled(curr < MAX_JUZ);

        btnPrevious.setOnClickListener(v -> navigateJuz(-1));
        btnNext.setOnClickListener(v -> navigateJuz(+1));

        playPauseButton.setOnClickListener(v -> toggleAudio());
        bookmarkButton.setOnClickListener(v -> toggleBookmark());
        btnBack.setOnClickListener(v -> finish());

        audioSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);}}
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });}
    private int extractJuzNumber(String fileName) {
        try {
            String[] parts = fileName.split("/");
            String lastPart = parts[parts.length - 1];
            String withoutExt = lastPart.replace(".pdf", "").replace(".mp3", "");
            if (withoutExt.length() >= 3) {
                String lastThreeDigits = withoutExt.substring(withoutExt.length() - 3);
                return Integer.parseInt(lastThreeDigits); }
        } catch (Exception e) {
            e.printStackTrace(); }
        return MIN_JUZ; }
    private void navigateJuz(int delta) {
        int curr = extractJuzNumber(pdfFile);
        int next = Math.max(MIN_JUZ, Math.min(MAX_JUZ, curr + delta));
        if (next == curr) {
            Toast.makeText(this, "No more Juz", Toast.LENGTH_SHORT).show();
            return;
        }
        String nextStr = String.format("%03d", next);
        String nextPdf = BASE_URL + "pdf/" + nextStr + ".pdf";
        String nextAudio = BASE_URL + "audio/" + nextStr + ".mp3";
        String nextName = "Juz " + next;
        String nextJuzName = JUZ_NAMES[next - 1];
        Intent intent = new Intent(this, JuzDetailActivity.class);
        intent.putExtra("pdfFile", nextPdf);
        intent.putExtra("audioFile", nextAudio);
        intent.putExtra("juzName", nextJuzName);

        finish(); //finish current activity
        startActivity(intent); //start new with next juz data
    }
    private void loadPdfFromUrl(String pdfUrl) {
        new Thread(() -> {
            try {
                String fileName = getFileNameFromUrl(pdfUrl);
                File file = new File(getFilesDir(), fileName);

                if (!file.exists()) {
                    downloadFile(this, pdfUrl, fileName);
                }
                runOnUiThread(() -> pdfView.fromFile(file).load());

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "PDF load failed", Toast.LENGTH_SHORT).show());
            }
        }).start();  }
    private void setupAudio(String audioUrl) {
        new Thread(() -> {
            try {
                String fileName = getFileNameFromUrl(audioUrl);
                File file = new File(getFilesDir(), fileName);
                if (!file.exists()) {
                    downloadFile(this, audioUrl, fileName);}
                runOnUiThread(() -> {
                    releaseAudio();
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(file.getAbsolutePath());
                        mediaPlayer.setOnPreparedListener(mp -> audioSeekBar.setMax(mediaPlayer.getDuration()));
                        mediaPlayer.setOnCompletionListener(mp -> {
                            playPauseButton.setImageResource(R.drawable.play);
                            handler.removeCallbacksAndMessages(null);
                            isPlaying = false;  });
                        mediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to load audio", Toast.LENGTH_SHORT).show();
                    }});
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Audio load failed", Toast.LENGTH_SHORT).show());
            }
        }).start(); }
    private void resetAndSetupAudio(String audioFile) {
        releaseAudio();
        setupAudio(audioFile);
        playPauseButton.setImageResource(R.drawable.play);
        isPlaying = false; }
    private void releaseAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null); }
    private void toggleAudio() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playPauseButton.setImageResource(R.drawable.play);
        } else {
            mediaPlayer.start();
            playPauseButton.setImageResource(R.drawable.pause_button);
            updateSeekBar();
        }}
    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    audioSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 500);  }}
        }, 500); }
    private void toggleBookmark() {
        isBookmarked = !isBookmarked;
        sharedPreferences.edit().putBoolean(pdfFile, isBookmarked).apply();
        updateBookmarkIcon();
        Toast.makeText(this, isBookmarked ? "Bookmarked" : "Removed", Toast.LENGTH_SHORT).show();}
    private void updateBookmarkIcon() {
        bookmarkButton.setImageResource(
                isBookmarked ? R.drawable.bookmark_filled : R.drawable.bookmark_icon
        );}
    private String getFileNameFromUrl(String url) {
        if (url == null) return "";
        return url.substring(url.lastIndexOf('/') + 1); }
    public static void downloadFile(Context context, String fileUrl, String fileName) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();
        FileOutputStream output = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead); }
        output.close();
        input.close();
        connection.disconnect();
        Log.d(TAG, fileName + " downloaded to internal storage."); }
    @Override
    protected void onDestroy() {
        releaseAudio();
        super.onDestroy(); }}
