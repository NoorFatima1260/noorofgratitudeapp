package noorofgratitute.com;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import android.Manifest;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.io.File;
import java.util.Calendar;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private EditText etFullName, etBio, etQuote, etDob, etInterests, etLocation;
    private Spinner spGender;
    private ImageView profileImageView, ivCalendar, btnBack;
    private MaterialButton editProfileButton;
    private Uri imageUri;
    private TextView nameTextView;
    private GratitudeService apiService;
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_JWT_ACCESS_TOKEN = "access_token";
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AuthUtils.isUserLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_profile);
        nameTextView = findViewById(R.id.nameTextView);
        ivCalendar = findViewById(R.id.ivCalendar);
        etFullName = findViewById(R.id.etFullName);
        etBio = findViewById(R.id.etBio);
        etQuote = findViewById(R.id.etQuote);
        etDob = findViewById(R.id.etDob);
        etInterests = findViewById(R.id.etInterests);
        etLocation = findViewById(R.id.etLocation);
        profileImageView = findViewById(R.id.profileImageView);
        editProfileButton = findViewById(R.id.editProfileButton);
        spGender = findViewById(R.id.spGender);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        profileImageView.setImageURI(imageUri);
                    }
                });

        String[] genderOptions = {"Male", "Female", "Other"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderOptions);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(genderAdapter);
        apiService = ApiClientpro.getClient().create(GratitudeService.class);
        exchangeFirebaseTokenForJwtThenLoadProfile();
        profileImageView.setOnClickListener(v -> checkStoragePermission());
        editProfileButton.setOnClickListener(v -> updateProfileWithAutoRefresh());
        ivCalendar.setOnClickListener(v -> showDatePicker());
        etDob.setOnClickListener(v -> showDatePicker());
    }
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String dob = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth);
            etDob.setText(dob);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 101);
            } else {
                pickImageFromGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
            } else {
                pickImageFromGallery();
            } } }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode == 101 || requestCode == 102) && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImageFromGallery();
        } else {
            Toast.makeText(this, "Storage permission is required to choose a profile picture", Toast.LENGTH_SHORT).show();
        } }
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void exchangeFirebaseTokenForJwtThenLoadProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return; }
        user.getIdToken(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String firebaseIdToken = task.getResult().getToken();
                if (firebaseIdToken == null) {
                    Toast.makeText(this, "Firebase token not found", Toast.LENGTH_SHORT).show();
                    return; }
                apiService.sendFirebaseToken(new FirebaseTokenRequest(firebaseIdToken)).enqueue(new Callback<FirebaseTokenResponse>() {
                    @Override
                    public void onResponse(Call<FirebaseTokenResponse> call, Response<FirebaseTokenResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String jwt = response.body().getAccess();
                            saveJwtToken(jwt);
                            ApiClientpro.setIdToken(jwt);
                            loadProfileWithAutoRefresh();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Token exchange failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        } }
                    @Override
                    public void onFailure(Call<FirebaseTokenResponse> call, Throwable t) {
                        Toast.makeText(ProfileActivity.this, "Token exchange error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    } });
            } else {
                Toast.makeText(this, "Firebase token error", Toast.LENGTH_SHORT).show();
            } }); }
    private void saveJwtToken(String token) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(KEY_JWT_ACCESS_TOKEN, token).apply();
    }
    private String getJwtToken() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(KEY_JWT_ACCESS_TOKEN, null);
    }
    private void loadProfileWithAutoRefresh() {
        String token = getJwtToken();
        if (token == null) {
            exchangeFirebaseTokenForJwtThenLoadProfile();
            return;
        }
        apiService.getProfile("Bearer " + token).enqueue(new Callback<GratitudeProfileRequest>() {
            @Override
            public void onResponse(Call<GratitudeProfileRequest> call, Response<GratitudeProfileRequest> response) {
                if (response.isSuccessful() && response.body() != null) {
                    populateProfileFields(response.body());
                } else if (response.code() == 401) {
                    exchangeFirebaseTokenForJwtThenLoadProfile();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile: " + response.code(), Toast.LENGTH_SHORT).show();
                } }
            @Override
            public void onFailure(Call<GratitudeProfileRequest> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Profile load error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            } }); }
    private void populateProfileFields(GratitudeProfileRequest profile) {
        etFullName.setText(profile.getFull_name());
        etBio.setText(profile.getBio());
        etQuote.setText(profile.getFavorite_quote());
        etDob.setText(profile.getDob());
        etInterests.setText(profile.getInterests());
        etLocation.setText(profile.getLocation());
        nameTextView.setText(profile.getFull_name());
        if (profile.getProfile_image_url() != null) {
            Glide.with(this).load(profile.getProfile_image_url()).into(profileImageView);
        }
        if (profile.getGender() != null) {
            int pos = ((ArrayAdapter<String>) spGender.getAdapter()).getPosition(profile.getGender());
            spGender.setSelection(pos);
        } }
    private void updateProfileWithAutoRefresh() {
        String token = getJwtToken();
        if (token == null) {
            exchangeFirebaseTokenForJwtThenLoadProfile();
            return;
        }
        updateProfile(token, new UpdateCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                loadProfileWithAutoRefresh();
            }
            @Override
            public void onFailure(int code, String message) {
                if (code == 401) {
                    exchangeFirebaseTokenForJwtThenLoadProfile();
                } else {
                    Toast.makeText(ProfileActivity.this, "Update failed: " + code + " " + message, Toast.LENGTH_SHORT).show();
                } } }); }
    private interface UpdateCallback {
        void onSuccess();
        void onFailure(int code, String message);
    }
    private void updateProfile(String token, UpdateCallback callback) {
        RequestBody fullName = RequestBody.create(etFullName.getText().toString(), MediaType.parse("text/plain"));
        RequestBody bio = RequestBody.create(etBio.getText().toString(), MediaType.parse("text/plain"));
        RequestBody quote = RequestBody.create(etQuote.getText().toString(), MediaType.parse("text/plain"));
        RequestBody gender = RequestBody.create(spGender.getSelectedItem().toString(), MediaType.parse("text/plain"));
        RequestBody dob = RequestBody.create(etDob.getText().toString(), MediaType.parse("text/plain"));
        RequestBody location = RequestBody.create(etLocation.getText().toString(), MediaType.parse("text/plain"));
        RequestBody interests = RequestBody.create(etInterests.getText().toString(), MediaType.parse("text/plain"));
        MultipartBody.Part imagePart = null;
        if (imageUri != null) {
            String imagePath = FileUtils.getPath(this, imageUri);
            if (imagePath != null) {
                File file = new File(imagePath);
                RequestBody imageBody = RequestBody.create(file, MediaType.parse("image/*"));
                imagePart = MultipartBody.Part.createFormData("profile_image", file.getName(), imageBody);
            } }
        apiService.updateProfile("Bearer " + token, fullName, bio, quote, gender, dob, location, interests, imagePart)
                .enqueue(new Callback<GratitudeProfileRequest>() {
                    @Override
                    public void onResponse(Call<GratitudeProfileRequest> call, Response<GratitudeProfileRequest> response) {
                        if (response.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            callback.onFailure(response.code(), response.message());
                        } }
                    @Override
                    public void onFailure(Call<GratitudeProfileRequest> call, Throwable t) {
                        Toast.makeText(ProfileActivity.this, "Update error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    } }); } }
