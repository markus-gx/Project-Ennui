package at.ennui.backend.offers.model;

import at.ennui.backend.offers.model.googleapi.Review;
import at.ennui.backend.offers.model.googleapi.TimePeriod;

import java.util.List;

/**
 * Created by Martin Singer on 19.09.2017.
 */
public class OfferDto {
    private LocationDetails locationDetails;
    private String international_phone_number;
    private String id;
    private String name;
    private boolean open_now;
    private List<TimePeriod> periods;
    private List<String> weekday_text;
    private double rating;
    private List<Review> reviews;
    private String website;
    private String reference;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocationDetails getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(LocationDetails locationDetails) {
        this.locationDetails = locationDetails;
    }

    public String getInternational_phone_number() {
        return international_phone_number;
    }

    public void setInternational_phone_number(String international_phone_number) {
        this.international_phone_number = international_phone_number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpen_now() {
        return open_now;
    }

    public void setOpen_now(boolean open_now) {
        this.open_now = open_now;
    }

    public List<TimePeriod> getPeriods() {
        return periods;
    }

    public void setPeriods(List<TimePeriod> periods) {
        this.periods = periods;
    }

    public List<String> getWeekday_text() {
        return weekday_text;
    }

    public void setWeekday_text(List<String> weekday_text) {
        this.weekday_text = weekday_text;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
