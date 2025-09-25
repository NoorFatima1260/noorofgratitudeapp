package noorofgratitute.com;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
public interface ApiServicesighnup
{
    @POST("api/register/")
    Call<RegisterResponsesignup> registerUser(@Body RegisterRequestsignup request);}

