package noorofgratitute.com;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.Calendar;

public class NotificationsActivity extends AppCompatActivity {
    private Switch switchGratitude, switchMood, switchRecitation, switchSchedule, switchDua, switchCalendar;
    private TextView tvGratitudeTime, tvMoodTime, tvRecitationTime, tvScheduleTime, tvDuaTime;
    private ImageView ivGratitudeTime,btnBack, ivMoodTime, ivRecitationTime, ivScheduleTime, ivDuaTime;
    private Spinner spinnerCalendarNotify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseMessaging.getInstance().subscribeToTopic("Notification")
                .addOnCompleteListener(task -> {
                    String msg = task.isSuccessful() ? "Subscribed to Notification" : "Subscription failed";
                    Log.d("FCM", msg);
                });

        setContentView(R.layout.activity_notifications);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> finish());
        //views
        btnBack = findViewById(R.id.btnBack);
        tvGratitudeTime = findViewById(R.id.tv_gratitude_time);
        tvMoodTime = findViewById(R.id.tv_mood_time);
        tvRecitationTime = findViewById(R.id.tv_recitation_time);
        tvScheduleTime = findViewById(R.id.tv_schedule_time);
        tvDuaTime = findViewById(R.id.tv_dua_time);
        ivGratitudeTime = findViewById(R.id.iv_gratitude_time);
        ivMoodTime = findViewById(R.id.iv_mood_time);
        ivRecitationTime = findViewById(R.id.iv_recitation_time);
        ivScheduleTime = findViewById(R.id.iv_schedule_time);
        ivDuaTime = findViewById(R.id.iv_dua_time);
        switchGratitude = findViewById(R.id.switch_gratitude);
        switchMood = findViewById(R.id.switch_mood);
        switchRecitation = findViewById(R.id.switch_recitation);
        switchSchedule = findViewById(R.id.switch_schedule);
        switchDua = findViewById(R.id.switch_dua);
        switchCalendar = findViewById(R.id.switch_calendar);
        spinnerCalendarNotify = findViewById(R.id.spinner_calendar_notify);
        loadPreferences();
        //time picker dialog for all time pickers
        ivGratitudeTime.setOnClickListener(view -> showTimePicker(tvGratitudeTime, "Gratitude"));
        ivMoodTime.setOnClickListener(view -> showTimePicker(tvMoodTime, "Mood"));
        ivRecitationTime.setOnClickListener(view -> showTimePicker(tvRecitationTime, "Recitation"));
        ivScheduleTime.setOnClickListener(view -> showTimePicker(tvScheduleTime, "Schedule"));
        ivDuaTime.setOnClickListener(view -> showTimePicker(tvDuaTime, "Dua"));
        //switch change listeners
        switchGratitude.setOnCheckedChangeListener((buttonView, isChecked) -> handleSwitchChange(isChecked, "Gratitude"));
        switchMood.setOnCheckedChangeListener((buttonView, isChecked) -> handleSwitchChange(isChecked, "Mood"));
        switchRecitation.setOnCheckedChangeListener((buttonView, isChecked) -> handleSwitchChange(isChecked, "Recitation"));
        switchSchedule.setOnCheckedChangeListener((buttonView, isChecked) -> handleSwitchChange(isChecked, "Schedule"));
        switchDua.setOnCheckedChangeListener((buttonView, isChecked) -> handleSwitchChange(isChecked, "Dua"));
        switchCalendar.setOnCheckedChangeListener((buttonView, isChecked) -> handleSwitchChange(isChecked, "Calendar"));
        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }
    private void showTimePicker(TextView textView, String reminderType) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            String time = String.format("%02d:%02d", selectedHour, selectedMinute);
            textView.setText(time);
            //saving the selected time in sharedpreferences
            SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(reminderType.toLowerCase() + "_time", time);
            editor.apply();
            //if the switch is enabled alarm is set
            if (getSwitchForReminder(reminderType).isChecked()) {
                setAlarm(selectedHour, selectedMinute, reminderType);
            }
        }, hour, minute, true);
        timePickerDialog.show(); }
    private Switch getSwitchForReminder(String reminderType) {
        switch (reminderType) {
            case "Gratitude": return switchGratitude;
            case "Mood": return switchMood;
            case "Recitation": return switchRecitation;
            case "Schedule": return switchSchedule;
            case "Dua": return switchDua;
            case "Calendar": return switchCalendar;
            default: return null;
        }}
    private void loadPreferences() {
        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        switchGratitude.setChecked(preferences.getBoolean("gratitude_switch", false));
        switchMood.setChecked(preferences.getBoolean("mood_switch", false));
        switchRecitation.setChecked(preferences.getBoolean("recitation_switch", false));
        switchSchedule.setChecked(preferences.getBoolean("schedule_switch", false));
        switchDua.setChecked(preferences.getBoolean("dua_switch", false));
        switchCalendar.setChecked(preferences.getBoolean("calendar_switch", false));

        tvGratitudeTime.setText(preferences.getString("gratitude_time", "Set Time"));
        tvMoodTime.setText(preferences.getString("mood_time", "Set Time"));
        tvRecitationTime.setText(preferences.getString("recitation_time", "Set Time"));
        tvScheduleTime.setText(preferences.getString("schedule_time", "Set Time"));
        tvDuaTime.setText(preferences.getString("dua_time", "Set Time"));
        // Re-setting alarms
        if (switchGratitude.isChecked()) {
            String time = tvGratitudeTime.getText().toString();
            if (!time.equals("Set Time")) {
                String[] timeParts = time.split(":");
                setAlarm(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]), "Gratitude");
            } }
        if (switchMood.isChecked()) {
            String time = tvMoodTime.getText().toString();
            if (!time.equals("Set Time")) {
                String[] timeParts = time.split(":");
                setAlarm(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]), "Mood");
            } } }
    private void savePreferences() {
        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("gratitude_switch", switchGratitude.isChecked());
        editor.putBoolean("mood_switch", switchMood.isChecked());
        editor.putBoolean("recitation_switch", switchRecitation.isChecked());
        editor.putBoolean("schedule_switch", switchSchedule.isChecked());
        editor.putBoolean("dua_switch", switchDua.isChecked());
        editor.putBoolean("calendar_switch", switchCalendar.isChecked());
        editor.apply(); }
    private void handleSwitchChange(boolean isChecked, String reminderType) {
        if (isChecked) {
            //reminder enabled
            Toast.makeText(this, reminderType + " reminder enabled", Toast.LENGTH_SHORT).show();
            String time = getTimeForReminder(reminderType);
            if (!time.equals("Set Time")) {
                String[] timeParts = time.split(":");
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);
                setAlarm(hour, minute, reminderType);
            }
        } else {
            //reminder disabled canceling the alarm
            Toast.makeText(this, reminderType + " reminder disabled", Toast.LENGTH_SHORT).show();
            cancelAlarm(reminderType);
        }
        savePreferences(); }
    private void cancelAlarm(String reminderType) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("reminder_type", reminderType);
        int requestCode = reminderType.hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(this, reminderType + " alarm canceled", Toast.LENGTH_SHORT).show();
        } }
    private String getTimeForReminder(String reminderType) {
        switch (reminderType) {
            case "Gratitude": return tvGratitudeTime.getText().toString();
            case "Mood": return tvMoodTime.getText().toString();
            case "Recitation": return tvRecitationTime.getText().toString();
            case "Schedule": return tvScheduleTime.getText().toString();
            case "Dua": return tvDuaTime.getText().toString();
            default: return "00:00";
        } }
    private void setAlarm(int hour, int minute, String reminderType) {
        //cancel any alarm for reminder type
        cancelAlarm(reminderType);
        //set the new alarm
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("reminder_type", reminderType);
        int requestCode = reminderType.hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } }
    private void setReminderAlarm(int hour, int minute, String reminderType) {
        cancelExistingAlarm(reminderType); // Cancel any existing alarm
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        //alarm intent
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("reminder_type", reminderType);
        int requestCode = reminderType.hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        //set alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } }
    private void cancelExistingAlarm(String reminderType) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("reminder_type", reminderType);
        int requestCode = reminderType.hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        } } }
