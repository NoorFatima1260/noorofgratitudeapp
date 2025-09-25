package noorofgratitute.com;
import android.os.Build;

import androidx.annotation.RequiresApi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
@RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
public class ApiClientpro {
    private static final String BASE_URL = "http://10.0.2.2:8000/";
    private static String idToken;
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder();
                if (idToken != null && !idToken.isEmpty()) {
                    builder.header("Authorization", "Bearer " + idToken);}
                Request request = builder.method(original.method(), original.body()).build();
                return chain.proceed(request);})
            .build();
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    public static void setIdToken(String token) {
        idToken = token;
    }
    public static Retrofit getClient() {
        return retrofit;
    }}

