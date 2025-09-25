package noorofgratitute.com;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class GratitudeDetailsActivity extends AppCompatActivity {
    private TextInputEditText etGratitude;
    private Button btnSave;
    private ImageButton btnShare, btnBack ,pickFileButton;
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final int PICK_FILE_REQUEST_CODE = 101;
    private static final String KEY_JWT_ACCESS_TOKEN = "access_token";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseMessaging.getInstance().subscribeToTopic("gratitude_reminders")
                .addOnCompleteListener(task -> {
                    String msg = task.isSuccessful() ? "Subscribed to gratitude_reminders" : "Subscription failed";
                    Log.d("FCM", msg);
                });
        setContentView(R.layout.activity_gratitude_details);

        pickFileButton = findViewById(R.id.btn_pick_from_gallery);
        etGratitude = findViewById(R.id.et_gratitude);
        btnSave = findViewById(R.id.btn_save_gratitude);
        btnShare = findViewById(R.id.btn_share_gratitude);
        btnBack = findViewById(R.id.btnBack);

        btnSave.setOnClickListener(view -> {
            String text = etGratitude.getText().toString().trim();
            if (!text.isEmpty()) {
                showSaveDialog(text);
            } else {
                Toast.makeText(this, "Please write something before saving.", Toast.LENGTH_SHORT).show();
            }});
        pickFileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*"); // Accept all types: images, videos, pdfs
            String[] mimeTypes = {"image/*", "video/*", "application/pdf"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST_CODE);
        });
        btnShare.setOnClickListener(view -> {
            String text = etGratitude.getText().toString().trim();
            if (!text.isEmpty()) {
                shareGratitude(text);
            } else {
                Toast.makeText(this, "Write something to share.", Toast.LENGTH_SHORT).show();
            }});
        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });}
    private Uri selectedFileUri;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            Toast.makeText(this, "File selected: " + selectedFileUri.getPath(), Toast.LENGTH_SHORT).show();
        }}
    private void showSaveDialog(String gratitudeText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Share Gratitude");
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_save_gratitude, null);
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);
        RadioButton rbPublic = dialogView.findViewById(R.id.rbPublic);
        RadioButton rbPrivate = dialogView.findViewById(R.id.rbPrivate);
        builder.setView(dialogView);
        builder.setPositiveButton("Post", (dialog, which) -> {
            if (rbPublic.isChecked()) {
                sendPostWithAutoTokenRefresh(gratitudeText);
            } else if (rbPrivate.isChecked()) {
                saveLocally(gratitudeText);
            } else {
                Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show();
            }});

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();}
    private void saveLocally(String text) {
        GratitudeEntryPrivately dao = new GratitudeEntryPrivately(this);
        dao.open();
        long result = dao.insertGratitudeEntryPrivately(
                java.text.DateFormat.getDateTimeInstance().format(new java.util.Date()), text);
        dao.close();

        if (result != -1) {
            updateStreak();  //still fine
            Toast.makeText(this, "Gratitude saved privately!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error saving gratitude!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateStreak() {
        SharedPreferences prefs = getSharedPreferences("GratitudePrefs", MODE_PRIVATE);
        long lastEntryTime = prefs.getLong("LastEntryTime", 0);
        long currentTime = System.currentTimeMillis();

        int currentStreak = prefs.getInt("StreakCount", 0);
        long oneDay = 24 * 60 * 60 * 1000;

        if (lastEntryTime > 0) {
            long diff = currentTime - lastEntryTime;
            if (diff >= oneDay && diff < 2 * oneDay) {
                currentStreak++;
            } else if (diff >= 2 * oneDay) {
                currentStreak = 1;
            }
        } else {
            currentStreak = 1;
        }

        prefs.edit()
                .putInt("StreakCount", currentStreak)
                .putLong("LastEntryTime", currentTime)
                .apply();
    }

    private void shareGratitude(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "I'm grateful for: " + text);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
    private void sendPostWithAutoTokenRefresh(String text) {
        String jwt = getJwtToken();
        if (jwt == null) {
            exchangeFirebaseTokenForJwt(() -> sendPostToBackend(text, getJwtToken()));
        } else {
            sendPostToBackend(text, jwt);
        }}
    private void sendPostToBackend(String text, String jwtToken) {
        if (jwtToken == null) {
            Toast.makeText(this, "JWT token not available", Toast.LENGTH_SHORT).show();
            return;}
        ApiServiceCommunity apiService = ApiClientCommunity.getClient().create(ApiServiceCommunity.class);
        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"), text);
        RequestBody privacyBody = RequestBody.create(MediaType.parse("text/plain"), "public");
        MultipartBody.Part imagePart = null; // No image for now
        MultipartBody.Part filePart = null;

        if (selectedFileUri != null) {
            String mimeType = getContentResolver().getType(selectedFileUri);
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedFileUri);
                File tempFile = File.createTempFile("upload", getFileExtension(selectedFileUri), getCacheDir());
                OutputStream outputStream = new FileOutputStream(tempFile);
                byte[] buffer = new byte[4096];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                inputStream.close();
                outputStream.close();
                RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), tempFile);
                filePart = MultipartBody.Part.createFormData("file", tempFile.getName(), requestFile);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "File read failed", Toast.LENGTH_SHORT).show();
                return;
            }}
        apiService.createPost("Bearer " + jwtToken, contentBody, privacyBody, imagePart, filePart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.isSuccessful() && response.body() != null) {
                        //  INCREMENT PUBLIC ENTRY COUNT HERE
                        SharedPreferences prefs = getSharedPreferences("GratitudePrefs", MODE_PRIVATE);
                        int currentCount = prefs.getInt("PublicGratitudeCount", 0);
                        prefs.edit().putInt("PublicGratitudeCount", currentCount + 1).apply();
                    }
                            try {
                        String json = response.body().string();
                        Log.d("API_RESPONSE", "Response JSON: " + json);
                        JSONObject obj = new JSONObject(json);
                        JSONObject post = obj.getJSONObject("post");
                        int postId = post.optInt("id", -1);
                        String username = post.optString("username", "Unknown User");
                        String timestamp = post.optString("timestamp", "Unknown Time");
                        String imageUrl = post.optString("profile_image_url", null);

                        Intent intent = new Intent(GratitudeDetailsActivity.this, CommunitySupportGratitude.class);
                        intent.putExtra("gratitude_text", text);
                        intent.putExtra("username", username);
                        intent.putExtra("timestamp", timestamp);
                        intent.putExtra("image_url", imageUrl);
                        intent.putExtra("post_id", postId);
                        intent.putExtra("file_url", post.optString("file_url", null));
                        intent.putExtra("file_type", post.optString("file_type", null));
                        intent.putExtra("file_url", post.optString("file_url", null));
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(GratitudeDetailsActivity.this, "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();}
                } else if (response.code() == 401) {
                    exchangeFirebaseTokenForJwt(() -> sendPostToBackend(text, getJwtToken()));
                } else {
                    Toast.makeText(GratitudeDetailsActivity.this, "Post failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }}
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(GratitudeDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }});}
    private void exchangeFirebaseTokenForJwt(Runnable onSuccess) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;}
        user.getIdToken(true).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String firebaseToken = task.getResult().getToken();
                if (firebaseToken == null) {
                    Toast.makeText(this, "No Firebase token", Toast.LENGTH_SHORT).show();
                    return;}
                GratitudeService apiService = ApiClientpro.getClient().create(GratitudeService.class);
                apiService.sendFirebaseToken(new FirebaseTokenRequest(firebaseToken)).enqueue(new Callback<FirebaseTokenResponse>() {
                    @Override
                    public void onResponse(Call<FirebaseTokenResponse> call, Response<FirebaseTokenResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String jwt = response.body().getAccess();
                            saveJwtToken(jwt);
                            onSuccess.run();
                        } else {
                            Toast.makeText(GratitudeDetailsActivity.this, "Token exchange failed", Toast.LENGTH_SHORT).show();
                        }}
                    @Override
                    public void onFailure(Call<FirebaseTokenResponse> call, Throwable t) {
                        Toast.makeText(GratitudeDetailsActivity.this, "Exchange failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }});
            } else {
                Toast.makeText(this, "Firebase token error", Toast.LENGTH_SHORT).show();
            }});}
    private void saveJwtToken(String token) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(KEY_JWT_ACCESS_TOKEN, token).apply();
    }
    private String getJwtToken() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(KEY_JWT_ACCESS_TOKEN, null);
    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return "." + mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }}
