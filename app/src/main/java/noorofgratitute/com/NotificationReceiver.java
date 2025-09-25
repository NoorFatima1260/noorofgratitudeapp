package noorofgratitute.com;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "azan_notification_channel";
    @Override
    public void onReceive(Context context, Intent intent) {
        String reminderType = intent.getStringExtra("reminder_type");
        showNotification(context, reminderType);
    }
    private void showNotification(Context context, String reminderType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Azan Notifications", NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        //notification
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(reminderType + " Reminder")
                .setContentText("It's time for your " + reminderType + " reminder.")
                .setSmallIcon(android.R.drawable.ic_notification_overlay)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(createPendingIntentToCancelAlarm(context, reminderType)) // Cancel alarm on click
                .build();
        // Showing notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(reminderType.hashCode(), notification);
        }}
    private PendingIntent createPendingIntentToCancelAlarm(Context context, String reminderType) {
        // PendingIntent to cancel the alarm when clicked
        Intent cancelIntent = new Intent(context, AlarmReceiver.class);
        cancelIntent.putExtra("reminder_type", reminderType);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(
                context, reminderType.hashCode(), cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        return cancelPendingIntent;
    } }
