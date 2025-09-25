package noorofgratitute.com;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiServiceverse {
    @GET("api/verse/")
    Call<VerseOfTheDay> getVerseOfTheDay(@Query("date") String date);}



