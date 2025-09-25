package noorofgratitute.com;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.IntentSenderRequest;
import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private TextInputEditText emailEditText, passwordEditText;
    private MaterialButton loginButton;
    private TextView signUpTextView;
    private ImageButton btnBack, googleLoginBtn, facebookLoginBtn;

    private SignInClient oneTapClient;
    private TextView forgotPasswordTextView;

    private ActivityResultLauncher<IntentSenderRequest> googleSignInLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Views
        btnBack = findViewById(R.id.btnBack);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpTextView = findViewById(R.id.signUpTextView);
        googleLoginBtn = findViewById(R.id.googleLoginBtn);
        facebookLoginBtn = findViewById(R.id.facebookLoginBtn);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        // Firebase Messaging
        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
                .addOnCompleteListener(task -> {
                    String msg = task.isSuccessful() ? "Subscribed to all_users" : "Subscription failed";
                    Log.d("FCM", msg);
                });

        // Facebook SDK
        FacebookSdk.fullyInitialize();
        AppEventsLogger.activateApp(getApplication());

        callbackManager = CallbackManager.Factory.create();

        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance());
        System.setProperty("firebase.appcheck.debug", "true");

        forgotPasswordTextView.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

// Click listener
        loginButton.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // alag name
            if (currentUser != null) {
                logoutUser();
                loginButton.setText("Login"); // logout ke baad
            } else {
                loginWithEmailPassword();
                loginButton.setText("Logout"); // login ke baad
            }
        });
        // Button ka initial state
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            loginButton.setText("Logout");
        } else {
            loginButton.setText("Login");
        }
        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });




        signUpTextView.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class))
        );

        setupGoogleLogin();
        setupFacebookLogin();
    }


    private void setupGoogleLogin() {
        oneTapClient = Identity.getSignInClient(this);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        try {
                            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                            String idToken = credential.getGoogleIdToken();
                            if (idToken != null) {
                                AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                                FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                if (user != null) sendTokenToBackend(user);
                                            } else {
                                                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } catch (Exception e) {
                            Log.e("LoginActivity", "Google Sign-In failed", e);
                            Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        googleLoginBtn.setOnClickListener(v -> {
            // Modern One Tap request
            oneTapClient.beginSignIn(
                    BeginSignInRequest.builder()
                            .setGoogleIdTokenRequestOptions(
                                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                            .setSupported(true)
                                            .setServerClientId(getString(R.string.default_web_client_id))
                                            .setFilterByAuthorizedAccounts(false)
                                            .build()
                            )
                            .build()
            ).addOnSuccessListener(result -> {
                try {
                    googleSignInLauncher.launch(
                            new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender())
                                    .build()
                    );
                } catch (Exception e) {
                    Log.e("LoginActivity", "Launch Google Sign-In failed", e);
                }
            }).addOnFailureListener(e -> Log.e("LoginActivity", "Google Sign-In start failed", e));
        });
    }


    private void setupFacebookLogin() {
        facebookLoginBtn.setOnClickListener(v -> {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Toast.makeText(LoginActivity.this, "Facebook login cancelled", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException error) {
                    Toast.makeText(LoginActivity.this, "Facebook login failed", Toast.LENGTH_SHORT).show();
                    Log.e("FacebookLogin", error.getMessage());
                }
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Only Facebook needs this
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, fbTask -> {
                    if (fbTask.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) sendTokenToBackend(user);
                    } else {
                        Toast.makeText(this, "Facebook sign-in failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginWithEmailPassword() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Enter email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Enter password");
            return;
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) sendTokenToBackend(user);
                    } else {
                        Exception e = task.getException();
                        String errorMsg = (e != null) ? e.getMessage() : "Unknown error";
                        Log.e("LoginActivity", "Sign-in failed", e);
                        Toast.makeText(this, "Login failed: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void sendTokenToBackend(FirebaseUser user) {
        user.getIdToken(true).addOnCompleteListener(tokenTask -> {
            if (!tokenTask.isSuccessful()) {
                Log.e("LoginActivity", "Token fetch failed", tokenTask.getException());
                Toast.makeText(this, "Firebase token fetch failed", Toast.LENGTH_SHORT).show();
                return;
            }

            String idToken = tokenTask.getResult().getToken();
            Log.d("LoginActivity", "Token fetched: " + idToken);

            try {
                MasterKey masterKey = new MasterKey.Builder(this)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();
                SharedPreferences securePrefs = EncryptedSharedPreferences.create(
                        this, "secure_prefs", masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );
                securePrefs.edit().putString("firebase_id_token", idToken).apply();

                ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
                FirebaseTokenRequest request = new FirebaseTokenRequest(idToken);
                apiService.sendFirebaseToken(request).enqueue(new Callback<FirebaseTokenResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<FirebaseTokenResponse> call,
                                           @NonNull Response<FirebaseTokenResponse> response) {
                        Log.d("Retrofit", "Response code: " + response.code());
                        if (response.body() != null) {
                            Log.d("Retrofit", "Response body: " + response.body().toString());
                        } else {
                            Log.d("Retrofit", "Response body is null");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<FirebaseTokenResponse> call, @NonNull Throwable t) {
                        Log.e("Retrofit", "Network error", t);
                    }
                });

            } catch (Exception e) {
                Log.e("LoginActivity", "EncryptedPrefs error", e);
            }
        });
    }
    private void logoutUser() {
        // 1. Firebase logout
        FirebaseAuth.getInstance().signOut();

        // 2. Google logout
        if (oneTapClient != null) {
            oneTapClient.signOut();
        }

        // 3. Facebook logout
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }

        // 4. Clear secure prefs
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
            Log.e("LoginActivity", "EncryptedPrefs error", e);
        }

        // 5. Notify backend
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = (user != null) ? user.getUid() : "unknown";

        ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
        apiService.logoutUser(new LogoutRequest(uid)).enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                Log.d("Retrofit", "Logout response: " + response.code());
                if (response.body() != null) {
                    Log.d("Retrofit", "Status: " + response.body().getStatus());
                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                Log.e("Retrofit", "Logout request failed", t);
            }
        });

        // 6. Show logout popup
        showPopupDialog("Logout", "You have successfully logged out.");
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
        closeBtn.setOnClickListener(v -> alert.dismiss());
    }
}
