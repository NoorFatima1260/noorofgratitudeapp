package noorofgratitute.com;
public class SettingsItem {
    private String title;
    private boolean isCategory;
    public SettingsItem(String title, boolean isCategory) {
        this.title = title;
        this.isCategory = isCategory;
    }
    public String getTitle() {
        return title;
    }

    public boolean isCategory() {
        return isCategory;
    }
}
