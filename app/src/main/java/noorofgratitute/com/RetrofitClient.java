package noorofgratitute.com;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class RetrofitClient {
    private static Retrofit retrofitApi = null;
    private static Retrofit retrofitAladhan = null;
    public static Retrofit getClient() {
        if (retrofitApi == null) {
            retrofitApi = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8000/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitApi;
    }
    public static Retrofit getAladhanClient() {
        if (retrofitAladhan == null) {
            retrofitAladhan = new Retrofit.Builder()
                    .baseUrl("https://api.aladhan.com/v1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitAladhan;
    } }
