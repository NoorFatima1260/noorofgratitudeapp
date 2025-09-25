package noorofgratitute.com;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MoodSyncWorker extends Worker {

    public MoodSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        DailyMoodDAO dao = new DailyMoodDAO(getApplicationContext());
        dao.open();
        List<MoodEntry> unsyncedMoods = dao.getUnsyncedMoods();

        if (unsyncedMoods.isEmpty()) {
            dao.close();
            return Result.success();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            dao.close();
            return Result.retry();
        }

        try {
            // Force token refresh and wait until ready
            String idToken = Tasks.await(user.getIdToken(true)).getToken();

            MoodApi api = RetrofitClientMood.getClient().create(MoodApi.class);

            for (MoodEntry mood : unsyncedMoods) {
                Call<MoodEntry> call = api.submitMood("Bearer " + idToken, mood);
                Response<MoodEntry> response = call.execute(); // synchronous network call
                if (response.isSuccessful()) {
                    dao.markAsSynced(mood.getDate());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            dao.close();
            return Result.retry(); // retry later if anything fails
        }

        dao.close();
        return Result.success(); // <-- add this
    }

}
