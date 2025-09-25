package noorofgratitute.com;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import java.util.List;
public interface MoodApi {
    @GET("submit-mood/")
    Call<List<MoodEntry>> getMoods(@Header("Authorization") String token);

    @POST("submit-mood/")
    Call<MoodEntry> submitMood(
            @Header("Authorization") String authToken,
            @Body MoodEntry moodEntry
    );
}