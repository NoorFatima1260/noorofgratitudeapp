package noorofgratitute.com;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
public interface IslamicEventService {
    @GET("islamic-events/")  //endpoint
    Call<List<IslamicEvent>> getIslamicEvents();}
