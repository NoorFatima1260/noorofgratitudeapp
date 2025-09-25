package noorofgratitute.com;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
public interface QuranApiService {
    @GET("api/surahs/")
    Call<List<Surah>> getSurahs();
    @GET("api/juzs/")
    Call<List<Juz>> getJuzs();
}
