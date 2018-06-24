package at.ennui.backend.offers.model.googleapi;

public class TimePeriodDay {
    private int day;
    private String time;

    public TimePeriodDay(){

    }

    public TimePeriodDay(int day, String time){
        this.day = day;
        this.time = time;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
