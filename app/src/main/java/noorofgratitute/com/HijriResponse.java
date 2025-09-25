package noorofgratitute.com;
public class HijriResponse {
    public Data data;
    public static class Data {
        public Hijri hijri;
    }

    public static class Hijri {
        public String day;
        public Month month;
        public String year;
    }

    public static class Month {
        public String en;
        public String ar;
    }
}
