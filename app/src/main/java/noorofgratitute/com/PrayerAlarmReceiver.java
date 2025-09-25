package noorofgratitute.com;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

public class PrayerAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String prayerName = intent.getStringExtra("prayer_name");

        //notify the user
        Toast.makeText(context, "It's time for " + prayerName + " prayer!", Toast.LENGTH_LONG).show();

        //play Azan sound
        playAzan(context); }
    private void playAzan(Context context) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.azan1);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(mp -> mp.release()); }
    //function to cancel the alarm
    public static void cancelAlarm(Context context, String prayerName) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, prayerName.hashCode(), new Intent(context, PrayerAlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT
        );
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(context, prayerName + " alarm canceled", Toast.LENGTH_SHORT).show();
        } } }

