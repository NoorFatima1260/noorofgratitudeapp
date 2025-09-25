package noorofgratitute.com;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
public class PrayerResponse {
    private Data data;
    public Data getData() {
        return data;
    }
    public static class Data {
        private Timings timings;
        public Timings getTimings() {
            return timings;
        }
    }
    public static class Timings {
        public String Fajr;
        public String Dhuhr;
        public String Asr;
        public String Maghrib;
        public String Isha;
        public Map<String, String> getAllTimingsIn12HourFormat() {
            return Map.of(
                    "Fajr", convertTo12HourFormat(Fajr),
                    "Dhuhr", convertTo12HourFormat(Dhuhr),
                    "Asr", convertTo12HourFormat(Asr),
                    "Maghrib", convertTo12HourFormat(Maghrib),
                    "Isha", convertTo12HourFormat(Isha)
            ); }
        // Helper function to convert time from 24-hour format to 12-hour format
        private String convertTo12HourFormat(String time24) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm");
                SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
                return outputFormat.format(inputFormat.parse(time24));
            } catch (ParseException e) {
                e.printStackTrace();
                return time24;  // If there's an error in parsing, return the original time
            }
        }
    }
}
