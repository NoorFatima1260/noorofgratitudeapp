package noorofgratitute.com;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
public interface GratitudeService {
    @POST("firebase-login/")
    Call<FirebaseTokenResponse> sendFirebaseToken(@Body FirebaseTokenRequest request);
    @Multipart
    @POST("profile/gratitude/")
    Call<GratitudeProfileRequest> updateProfile(
            @Header("Authorization") String authToken,
            @Part("full_name") RequestBody fullName,
            @Part("bio") RequestBody bio,
            @Part("favorite_quote") RequestBody quote,
            @Part("gender") RequestBody gender,
            @Part("dob") RequestBody dob,
            @Part("location") RequestBody location,
            @Part("interests") RequestBody interests,
            @Part MultipartBody.Part profileImage);
    @GET("profile/gratitude/")
    Call<GratitudeProfileRequest> getProfile(
            @Header("Authorization") String authToken);}

