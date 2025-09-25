package noorofgratitute.com;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Calendar;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrayerTimesAdapter extends RecyclerView.Adapter<PrayerTimesAdapter.ViewHolder> {
    private List<PrayerTime> prayerTimes;
    private Context context;
    private PrayerDbHelper dbHelper;

    public PrayerTimesAdapter(Context context, List<PrayerTime> prayerTimes) {
        this.context = context;
        this.prayerTimes = prayerTimes;
        this.dbHelper = new PrayerDbHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_prayer_time, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PrayerTime prayerTime = prayerTimes.get(position);
        holder.txtPrayerName.setText(prayerTime.getName());
        holder.txtPrayerTime.setText(prayerTime.getTime());

        // Set icon based on alarm state
        holder.imgAlarmBell.setImageResource(
                prayerTime.isAlarmSet() ?
                        android.R.drawable.ic_lock_silent_mode_off:
                        android.R.drawable.ic_lock_silent_mode
        );

        holder.imgAlarmBell.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition(); // Use bindingAdapterPosition
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            PrayerTime clickedPrayer = prayerTimes.get(adapterPosition);

            if (clickedPrayer.isAlarmSet()) {
                cancelAlarm(clickedPrayer);
                clickedPrayer.setAlarmSet(false);
                dbHelper.insertOrUpdatePrayerTime(clickedPrayer.getName(), clickedPrayer.getTime(), false);
                notifyItemChanged(adapterPosition);
            } else {
                setAlarm(clickedPrayer);
                clickedPrayer.setAlarmSet(true);
                dbHelper.insertOrUpdatePrayerTime(clickedPrayer.getName(), clickedPrayer.getTime(), true);
                notifyItemChanged(adapterPosition);
            }
        });
    }
        @Override
    public int getItemCount() {
        return prayerTimes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPrayerName, txtPrayerTime;
        ImageView imgAlarmBell;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPrayerName = itemView.findViewById(R.id.txtPrayerName);
            txtPrayerTime = itemView.findViewById(R.id.txtPrayerTime);
            imgAlarmBell = itemView.findViewById(R.id.imgAlarmBell);
        }
    }

    private void setAlarm(PrayerTime prayerTime) {
        String time = prayerTime.getTime();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            Date date = sdf.parse(time);

            SimpleDateFormat sdf24Hour = new SimpleDateFormat("HH:mm");
            String time24Hour = sdf24Hour.format(date);
            String[] timeParts = time24Hour.split(":");

            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            Intent alarmIntent = new Intent(context, PrayerAlarmReceiver.class);
            alarmIntent.putExtra("prayer_name", prayerTime.getName());

            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    prayerTime.hashCode(),
                    alarmIntent,
                    flags
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
                Toast.makeText(context, prayerTime.getName() + " alarm set for " + time, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to set alarm", Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelAlarm(PrayerTime prayerTime) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                prayerTime.hashCode(),
                new Intent(context, PrayerAlarmReceiver.class),
                flags
        );

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(context, prayerTime.getName() + " alarm canceled", Toast.LENGTH_SHORT).show();
        }
    }
}
