package noorofgratitute.com;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String reminderType = intent.getStringExtra("reminder_type");
        Toast.makeText(context, reminderType + " reminder triggered", Toast.LENGTH_SHORT).show();
        // Play Azan sound
        playAzan(context);
        startNotificationReceiver(context, reminderType);}
    private void playAzan(Context context) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.azan1);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(mp -> mp.release());}

    private void startNotificationReceiver(Context context, String reminderType) {
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra("reminder_type", reminderType);
        context.sendBroadcast(notificationIntent);}

    // function cancel alarm
    public static void cancelAlarm(Context context, String reminderType) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, reminderType.hashCode(), new Intent(context, AlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(context, reminderType + " alarm canceled", Toast.LENGTH_SHORT).show(); }}}
