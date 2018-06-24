package at.ennui.backend.offers.model.googleapi;

public class TimePeriod {
    private TimePeriodDay close;
    private TimePeriodDay open;

    public TimePeriodDay getClose() {
        return close;
    }

    public void setClose(TimePeriodDay close) {
        this.close = close;
    }

    public TimePeriodDay getOpen() {
        return open;
    }

    public void setOpen(TimePeriodDay open) {
        this.open = open;
    }
}
