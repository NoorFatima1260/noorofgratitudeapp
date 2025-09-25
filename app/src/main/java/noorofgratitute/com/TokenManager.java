package noorofgratitute.com;
import android.content.Context;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class TokenManager {
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_JWT_ACCESS_TOKEN = "access_token";
    public static String getToken(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_JWT_ACCESS_TOKEN, null);
    }
    public static void saveToken(Context context, String token) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_JWT_ACCESS_TOKEN, token).apply();
    }
    public static void getValidToken(Context context, TokenCallback callback) {
        String token = getToken(context);
        if (token != null) {
            callback.onTokenReady(token);
            return; }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.getIdToken(true).addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    String firebaseToken = task.getResult().getToken();
                    if (firebaseToken == null) {
                        callback.onError("No Firebase token found.");
                        return; }
                    GratitudeService apiService = ApiClientpro.getClient().create(GratitudeService.class);
                    apiService.sendFirebaseToken(new FirebaseTokenRequest(firebaseToken)).enqueue(new Callback<FirebaseTokenResponse>() {
                        @Override
                        public void onResponse(Call<FirebaseTokenResponse> call, Response<FirebaseTokenResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                String jwt = response.body().getAccess();
                                saveToken(context, jwt);
                                callback.onTokenReady(jwt);
                            } else {
                                callback.onError("JWT exchange failed.");
                            } }
                        @Override
                        public void onFailure(Call<FirebaseTokenResponse> call, Throwable t) {
                            callback.onError("Token exchange error: " + t.getMessage());
                        } });
                } else {
                    callback.onError("Failed to get Firebase ID token.");
                } });
        } else {
            callback.onError("Firebase user not found.");
        } }
    public interface TokenCallback {
        void onTokenReady(String token);
        void onError(String errorMessage);
        void onFailure();
    } }

