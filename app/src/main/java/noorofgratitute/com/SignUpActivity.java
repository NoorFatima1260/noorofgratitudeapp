package noorofgratitute.com;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText usernameEditText, userGmailEditText, passwordEditText;
    private MaterialButton signUpButton;
    private TextView errorTextView, loginTextView;
    private ImageButton btnBack, googleLoginBtn,facebookLoginBtn;
    private static final String BASE_URL = "http://10.0.2.2:8000/";
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 1001;
    private ApiServicesighnup apiService;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        usernameEditText = findViewById(R.id.usernameEditText);
        userGmailEditText = findViewById(R.id.userGmail);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        errorTextView = findViewById(R.id.errorTextView);
        loginTextView = findViewById(R.id.loginTextView);
        btnBack = findViewById(R.id.btn_back);
        googleLoginBtn = findViewById(R.id.googleLoginBtn);
        facebookLoginBtn = findViewById(R.id.facebookLoginBtn); 
        mAuth = FirebaseAuth.getInstance();
        FirebaseMessaging.getInstance().subscribeToTopic("Sign Up all_users")
                .addOnCompleteListener(task -> {
                    String msg = task.isSuccessful() ? "Subscribed to Sign Up all_users" : "Subscription failed";
                    Log.d("FCM", msg);
                });
        // Facebook SDK
        FacebookSdk.fullyInitialize();
        AppEventsLogger.activateApp(getApplication());
        // appcheck
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance());
        System.setProperty("firebase.appcheck.debug", "true");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User logged in
            signUpButton.setText("Sign Out");
            signUpButton.setOnClickListener(v -> logoutUser());
        } else {
            // User not logged in
            signUpButton.setText("Sign Up");
            signUpButton.setOnClickListener(v -> registerUser());
        }


        callbackManager = CallbackManager.Factory.create();

        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        loginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiServicesighnup.class);
        signUpButton.setOnClickListener(v -> registerUser());
        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleLoginBtn.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
        facebookLoginBtn.setOnClickListener(v -> {
            LoginManager.getInstance().logInWithReadPermissions(SignUpActivity.this, Arrays.asList("email", "public_profile"));
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }
                @Override
                public void onCancel() {
                    Toast.makeText(SignUpActivity.this, "Facebook SignUp cancelled.", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onError(FacebookException error) {
                    Log.e("FacebookSignUp", "Error: " + error.getMessage());
                    Toast.makeText(SignUpActivity.this, "Facebook SignUp failed.", Toast.LENGTH_SHORT).show();
                } }); });

       }
    private void logoutUser() {
        // Firebase logout
        FirebaseAuth.getInstance().signOut();

        // Google logout
        if (googleSignInClient != null) {
            googleSignInClient.signOut();
        }

        // Facebook logout
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }

        // Clear secure prefs
        try {
            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            SharedPreferences securePrefs = EncryptedSharedPreferences.create(
                    this, "secure_prefs", masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            securePrefs.edit().remove("firebase_id_token").apply();
        } catch (Exception e) {
            Log.e("SignUpActivity", "EncryptedPrefs error", e);
        }

        // Button text wapas Sign Up
        signUpButton.setText("Sign Up");
        signUpButton.setOnClickListener(v -> registerUser());

        showPopupDialog("Logout", "You have successfully logged out.", null);
    }

    private void storeTokenSecurely(String idToken) {
        try {
            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences securePrefs = EncryptedSharedPreferences.create(
                    this,
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            securePrefs.edit().putString("firebase_id_token", idToken).apply();
            Log.d("SignUpActivity", "Token stored securely");
        } catch (Exception e) {
            Log.e("SignUpActivity", "Failed to store token securely", e);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String googleIdToken = account.getIdToken();
                //1.exchange Google token for Firebase credential:
                AuthCredential credential =
                        GoogleAuthProvider.getCredential(googleIdToken, null);
                FirebaseAuth.getInstance()
                        .signInWithCredential(credential)
                        .addOnCompleteListener(this, fbTask -> {
                            if (fbTask.isSuccessful()) {
                                //2.Fetch the Firebase ID token:
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.getIdToken(true)
                                        .addOnCompleteListener(tokenTask -> {
                                            if (tokenTask.isSuccessful()) {
                                                String firebaseIdToken = tokenTask.getResult().getToken();
                                                sendTokenToBackend(firebaseIdToken);
                                            } else {
                                                Log.e("SignUpActivity", "Failed to get Firebase ID token", tokenTask.getException());
                                                Toast.makeText(this, "Firebase Auth failed", Toast.LENGTH_SHORT).show();
                                            } });
                            } else {
                                Log.e("SignUpActivity", "signInWithCredential failed", fbTask.getException());
                                Toast.makeText(this, "Firebase sign-Up failed", Toast.LENGTH_SHORT).show();
                            } });
            } catch (ApiException e) {
                Log.e("SignUpActivity", "Google Sign-Up failed", e);
                Toast.makeText(this, "Google Sign-Up Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } }}
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, fbTask -> {
                    if (fbTask.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.getIdToken(true)
                                .addOnCompleteListener(tokenTask -> {
                                    if (tokenTask.isSuccessful()) {
                                        String firebaseIdToken = tokenTask.getResult().getToken();
                                        sendTokenToBackend(firebaseIdToken);
                                    } else {
                                        Log.e("SignUpActivity", "Failed to get Firebase ID token", tokenTask.getException());
                                        Toast.makeText(this, "Firebase Auth failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Log.e("SignUpActivity", "signInWithCredential failed", fbTask.getException());
                        Toast.makeText(this, "Firebase sign-in failed", Toast.LENGTH_SHORT).show();
                    } }); }
    private void sendTokenToBackend(String idToken) {
        ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
        FirebaseTokenRequest request = new FirebaseTokenRequest(idToken);
        Call<FirebaseTokenResponse> call = apiService.sendFirebaseToken(request);
        call.enqueue(new Callback<FirebaseTokenResponse>() {
            @Override
            public void onResponse(@NonNull Call<FirebaseTokenResponse> call,
                                   @NonNull Response<FirebaseTokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String accessJwt = response.body().getAccess();
                    Toast.makeText(SignUpActivity.this,
                            "SignUp Success! Access Token: " + accessJwt,
                            Toast.LENGTH_SHORT).show();
                    //Save JWTs and proceed to MainActivity
                } else {
                    Toast.makeText(SignUpActivity.this,
                            "Auth Failed: HTTP " + response.code(),
                            Toast.LENGTH_SHORT).show();
                } }
            @Override
            public void onFailure(@NonNull Call<FirebaseTokenResponse> call,
                                  @NonNull Throwable t) {
                Toast.makeText(SignUpActivity.this,
                        "Network Error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            } }); }
    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String email = userGmailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorTextView.setText("Please fill all fields!");
            errorTextView.setVisibility(View.VISIBLE);
            return;
        }

        //Firebase Email/Password signup
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Update display name
                            firebaseUser.updateProfile(
                                    new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                            .setDisplayName(username)
                                            .build()
                            );

                            // Fetch Firebase token
                            firebaseUser.getIdToken(true).addOnCompleteListener(tokenTask -> {
                                if (tokenTask.isSuccessful()) {
                                    String firebaseIdToken = tokenTask.getResult().getToken();

                                    // Send to backend
                                    sendTokenToBackend(firebaseIdToken);

                                    // Store securely like login
                                    storeTokenSecurely(firebaseIdToken);

                                    // Show popup
                                    showPopupDialog("Sign Up Successful",
                                            "Welcome " + username + "! Your account has been created successfully.",
                                            () -> {
                                                // Optional: navigate to login after closing popup
                                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            });
                                }
                            });
                        }
                    } else {
                        errorTextView.setText("Firebase Signup Failed: " + task.getException().getMessage());
                        errorTextView.setVisibility(View.VISIBLE);
                    }
                });

    }
    private void showPopupDialog(String title, String message, Runnable onClose) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_popup, null);
        builder.setView(dialogView);
        TextView popupTitle = dialogView.findViewById(R.id.popupTitle);
        TextView popupMessage = dialogView.findViewById(R.id.popupMessage);
        Button closeBtn = dialogView.findViewById(R.id.closePopupBtn);
        popupTitle.setText(title);
        popupMessage.setText(message);
        AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alert.show();
        closeBtn.setOnClickListener(v -> {
            alert.dismiss();
            if (onClose != null) onClose.run();
        });
    }

    private void showPopupDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_popup, null);
        builder.setView(dialogView);
        TextView popupTitle = dialogView.findViewById(R.id.popupTitle);
        TextView popupMessage = dialogView.findViewById(R.id.popupMessage);
        Button closeBtn = dialogView.findViewById(R.id.closePopupBtn);
        popupTitle.setText(title);
        popupMessage.setText(message);
        AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alert.show();
        closeBtn.setOnClickListener(v -> alert.dismiss()); } }
