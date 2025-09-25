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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
public class SurahDetailActivity extends AppCompatActivity {
    private static final int MIN_SURAH = 1;
    private static final int MAX_SURAH = 114;
    private PDFView pdfView;
    private ImageButton playPauseButton, bookmarkButton, btnBack;
    private MaterialButton btnPrevious, btnNext;
    private SeekBar audioSeekBar;
    private TextView surahNameText;
    private static final String[] SURAH_NAMES = {
            "Al-Fatiha", "Al-Baqarah", "Aal-E-Imran", "An-Nisa", "Al-Ma'idah", "Al-An'am",
            "Al-A'raf", "Al-Anfal", "At-Tawbah", "Yunus", "Hud", "Yusuf", "Ar-Ra'd",
            "Ibrahim", "Al-Hijr", "An-Nahl", "Al-Isra", "Al-Kahf", "Maryam", "Ta-Ha",
            "Al-Anbiya", "Al-Hajj", "Al-Mu'minun", "An-Nur", "Al-Furqan", "Ash-Shu'ara",
            "An-Naml", "Al-Qasas", "Al-Ankabut", "Ar-Rum", "Luqman", "As-Sajda",
            "Al-Ahzab", "Saba", "Fatir", "Ya-Sin", "As-Saffat", "Sad", "Az-Zumar",
            "Ghafir", "Fussilat", "Ash-Shura", "Az-Zukhruf", "Ad-Dukhan", "Al-Jathiya",
            "Al-Ahqaf", "Muhammad", "Al-Fath", "Al-Hujurat", "Qaf", "Adh-Dhariyat",
            "At-Tur", "An-Najm", "Al-Qamar", "Ar-Rahman", "Al-Waqia", "Al-Hadid",
            "Al-Mujadila", "Al-Hashr", "Al-Mumtahanah", "As-Saff", "Al-Jumu'a",
            "Al-Munafiqun", "At-Taghabun", "At-Talaq", "At-Tahrim", "Al-Mulk", "Al-Qalam",
            "Al-Haqqa", "Al-Ma'arij", "Nuh", "Al-Jinn", "Al-Muzzammil", "Al-Muddaththir",
            "Al-Qiyamah", "Al-Insan", "Al-Mursalat", "An-Naba", "An-Nazi'at", "Abasa",
            "At-Takwir", "Al-Infitar", "Al-Mutaffifin", "Al-Inshiqaq", "Al-Buruj",
            "At-Tariq", "Al-A'la", "Al-Ghashiyah", "Al-Fajr", "Al-Balad", "Ash-Shams",
            "Al-Lail", "Ad-Duhaa", "Ash-Sharh", "At-Tin", "Al-Alaq", "Al-Qadr",
            "Al-Bayyina", "Az-Zalzalah", "Al-Adiyat", "Al-Qari'a", "At-Takathur",
            "Al-Asr", "Al-Humazah", "Al-Fil", "Quraish", "Al-Ma'un", "Al-Kawthar",
            "Al-Kafirun", "An-Nasr", "Al-Masad", "Al-Ikhlas", "Al-Falaq", "An-Nas"
    };
    private String audioFile, pdfFile;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private boolean isBookmarked = false;
    private Handler handler = new Handler();
    private SharedPreferences sharedPreferences;
    private static final String TAG = "SurahDetailActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surah_detail);
        //view
        pdfView = findViewById(R.id.pdfView);
        playPauseButton = findViewById(R.id.playPauseButton);
        bookmarkButton = findViewById(R.id.bookmarkButton);
        btnBack = findViewById(R.id.btnBack);
        audioSeekBar = findViewById(R.id.audioSeekBar);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        surahNameText = findViewById(R.id.surahNameText);
        //intent
        pdfFile = getIntent().getStringExtra("pdfFile");
        audioFile = getIntent().getStringExtra("audioFile");
        String surahName = getIntent().getStringExtra("surahName");
        surahNameText.setText(surahName);
        sharedPreferences = getSharedPreferences("Bookmarks", MODE_PRIVATE);
        isBookmarked = sharedPreferences.getBoolean(pdfFile, false);
        updateBookmarkIcon();
        loadPdfFromUrl(pdfFile);
        setupAudio(audioFile);
        //button listeners
        playPauseButton.setOnClickListener(v -> toggleAudio());
        bookmarkButton.setOnClickListener(v -> toggleBookmark());
        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        int curr = extractSurahNumber(pdfFile);
        btnPrevious.setEnabled(curr > MIN_SURAH);
        btnNext.setEnabled(curr < MAX_SURAH);
        btnPrevious.setOnClickListener(v -> navigateSurah(-1));
        btnNext.setOnClickListener(v -> navigateSurah(+1));
        audioSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                } }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        }); }
    // Extracting Surah number from filename/url (001.pdf)
    private int extractSurahNumber(String fileName) {
        try {
            String[] parts = fileName.split("/");
            String lastPart = parts[parts.length - 1];
            String withoutExt = lastPart.replace(".pdf", "").replace(".mp3", "");
            if (withoutExt.length() >= 3) {
                String lastThreeDigits = withoutExt.substring(withoutExt.length() - 3);
                return Integer.parseInt(lastThreeDigits);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MIN_SURAH;
    }
    //going to previous or next Surah
    private void navigateSurah(int delta) {
        int curr = extractSurahNumber(pdfFile);
        int next = Math.max(MIN_SURAH, Math.min(MAX_SURAH, curr + delta));
        if (next == curr) {
            Toast.makeText(this, "No more Surahs", Toast.LENGTH_SHORT).show();
            return;
        }

        String nextStr = String.format("%03d", next);
        String baseUrl = "http://192.168.0.103:8000/media/surahs/";
        String nextPdf = baseUrl + "pdf/" + nextStr + ".pdf";
        String nextAudio = baseUrl + "audio/" + nextStr + ".mp3";
        String nextSurahName = SURAH_NAMES[next - 1];

        Intent intent = new Intent(this, SurahDetailActivity.class);
        intent.putExtra("pdfFile", nextPdf);
        intent.putExtra("audioFile", nextAudio);
        intent.putExtra("surahName", nextSurahName);
        finish();
        startActivity(intent);
    }

    //loading PDF from URL
    private void loadPdfFromUrl(String pdfUrl) {
        new Thread(() -> {
            try {
                String fileName = getFileNameFromUrl(pdfUrl);
                File file = new File(getFilesDir(), fileName);
                if (!isFileExists(this, fileName)) {
                    downloadFile(this, pdfUrl, fileName);
                }
                runOnUiThread(() -> pdfView.fromFile(file).load());
                if (file.exists()) {
                    runOnUiThread(() -> pdfView.fromFile(file).load());
                } else {
                    InputStream input = new URL(pdfUrl).openStream();
                    FileOutputStream output = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = input.read(buffer)) > 0) {
                        output.write(buffer, 0, length);
                    }
                    input.close();
                    output.close();
                    runOnUiThread(() -> pdfView.fromFile(file).load());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "PDF load failed (check connection)", Toast.LENGTH_SHORT).show());
            }
        }).start(); }
    //setup audio from URL
    private void setupAudio(String audioUrl) {
        new Thread(() -> {
            try {
                String fileName = getFileNameFromUrl(audioUrl);
                File file = new File(getFilesDir(), fileName);

                if (!file.exists()) {
                    InputStream input = new URL(audioUrl).openStream();
                    FileOutputStream output = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = input.read(buffer)) > 0) {
                        output.write(buffer, 0, length);
                    }
                    input.close();
                    output.close();
                }
                runOnUiThread(() -> {
                    releaseAudio();
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(file.getAbsolutePath());
                        mediaPlayer.setOnPreparedListener(mp -> audioSeekBar.setMax(mediaPlayer.getDuration()));
                        mediaPlayer.setOnCompletionListener(mp -> {
                            playPauseButton.setImageResource(R.drawable.play);
                            handler.removeCallbacksAndMessages(null);
                            isPlaying = false;
                        });
                        mediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to load audio", Toast.LENGTH_SHORT).show();
                    } });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Audio load failed (check connection)", Toast.LENGTH_SHORT).show());
            }
        }).start(); }
    //extracting filename from URL
    private String getFileNameFromUrl(String url) {
        if (url == null) return "";
        return url.substring(url.lastIndexOf('/') + 1);
    }
    //toggle play/pause audio
    private void toggleAudio() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playPauseButton.setImageResource(R.drawable.play);
        } else {
            mediaPlayer.start();
            playPauseButton.setImageResource(R.drawable.pause_button);
            updateSeekBar();
        } }
    //update seekbar while audio plays
    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    audioSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 500);
                } }
        }, 500); }
    //release mediaplayer resources
    private void releaseAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null; }
        handler.removeCallbacksAndMessages(null);
    }
    //toggle bookmark status and save to sharedpreferences
    private void toggleBookmark() {
        isBookmarked = !isBookmarked;
        sharedPreferences.edit().putBoolean(pdfFile, isBookmarked).apply();
        updateBookmarkIcon();
        Toast.makeText(this, isBookmarked ? "Bookmarked" : "Removed", Toast.LENGTH_SHORT).show();
    }
    //update bookmark button icon
    private void updateBookmarkIcon() {
        bookmarkButton.setImageResource(
                isBookmarked ? R.drawable.bookmark_filled : R.drawable.bookmark_icon
        ); }
    @Override
    protected void onDestroy() {
        releaseAudio();
        super.onDestroy(); }
    public static boolean isFileExists(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        return file.exists();
    }
    public static void downloadFile(Context context, String fileUrl, String fileName) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Server returned HTTP " + connection.getResponseCode()
                    + " " + connection.getResponseMessage());
        }
        // Toast: Download start
        if (context instanceof SurahDetailActivity) {
            ((SurahDetailActivity) context).runOnUiThread(() ->
                    Toast.makeText(context, "Downloading " + fileName, Toast.LENGTH_SHORT).show()
            ); }
        if (context instanceof SurahDetailActivity) {
            ((SurahDetailActivity) context).runOnUiThread(() ->
                    Toast.makeText(context, "Download completed", Toast.LENGTH_SHORT).show()
            ); }
        InputStream input = connection.getInputStream();
        FileOutputStream output = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        output.close();
        input.close();
        connection.disconnect();
        Log.d(TAG, fileName + " downloaded to internal storage.");
    } }
