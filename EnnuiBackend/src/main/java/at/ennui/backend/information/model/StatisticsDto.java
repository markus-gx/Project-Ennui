package at.ennui.backend.information.model;

public class StatisticsDto {
    private long users;
    private long events;
    private Object countryWithEvents;

    public long getUsers() {
        return users;
    }

    public void setUsers(long users) {
        this.users = users;
    }

    public long getEvents() {
        return events;
    }

    public void setEvents(long events) {
        this.events = events;
    }

    public Object getCountryWithEvents() {
        return countryWithEvents;
    }

    public void setCountryWithEvents(Object countryWithEvents) {
        this.countryWithEvents = countryWithEvents;
    }
}
