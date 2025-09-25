package noorofgratitute.com;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlAdhanApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.aladhan.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
