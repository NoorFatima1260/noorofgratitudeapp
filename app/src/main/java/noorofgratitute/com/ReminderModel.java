package noorofgratitute.com;
public class ReminderModel {
    private String title;
    private String time;
    public ReminderModel(String title, String time) {
        this.title = title;
        this.time = time;
    }
    public String getTitle() {
        return title;
    }
    public String getTime() {
        return time;
    }
}

