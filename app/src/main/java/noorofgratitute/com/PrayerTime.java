package noorofgratitute.com;
public class PrayerTime {
    private String name;
    private String time;
    private boolean isAlarmSet;
    public PrayerTime(String name, String time, boolean isAlarmSet) {
        this.name = name;
        this.time = time;
        this.isAlarmSet = isAlarmSet;
    }
    public String getName() {
        return name;
    }
    public String getTime() {
        return time;
    }

    public boolean isAlarmSet() {
        return isAlarmSet;
    }

    public void setAlarmSet(boolean alarmSet) {
        isAlarmSet = alarmSet;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PrayerTime other = (PrayerTime) obj;
        return name != null && name.equals(other.name);
    }
}
