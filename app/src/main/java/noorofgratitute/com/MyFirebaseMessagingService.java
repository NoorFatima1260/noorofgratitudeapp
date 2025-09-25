package noorofgratitute.com;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "gratitude_channel";
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("FCM", "New token: " + token);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title = "New Gratitude Post";
        String body = "";

        if (remoteMessage.getData().size() > 0) {
            title = remoteMessage.getData().get("username") + " posted:";
            body = remoteMessage.getData().get("content");

            //broadcast update post list
            Intent broadcastIntent = new Intent("NEW_POST_RECEIVED");
            sendBroadcast(broadcastIntent);
        }
        Intent intent = new Intent(this, CommunitySupportGratitude.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_gratitude)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Gratitude Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel); }
        manager.notify(0, builder.build());
    }}