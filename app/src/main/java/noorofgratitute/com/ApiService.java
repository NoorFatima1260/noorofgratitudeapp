package noorofgratitute.com;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
public interface ApiService {
    @POST("firebase-login/")
    Call<FirebaseTokenResponse> sendFirebaseToken(@Body FirebaseTokenRequest request);

    @POST("api/logout/")
    Call<LogoutResponse> logoutUser(@Body LogoutRequest request); // POST body optional, lekin user id bhejna achha hai

}
