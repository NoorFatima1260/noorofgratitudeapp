package noorofgratitute.com;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface AladhanApiService {
    // Hijri Date Conversion API
    @GET("gToH")
    Call<HijriResponse> getHijriDate(
            @Query("date") String date
    );
    // Prayer Timings API
    @GET("timings")
    Call<PrayerResponse> getPrayerTimes(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("method") int method
    );
}