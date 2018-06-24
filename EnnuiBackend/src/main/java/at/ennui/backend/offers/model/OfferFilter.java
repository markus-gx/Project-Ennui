package at.ennui.backend.offers.model;

import at.ennui.backend.offers.configuration.OfferCategories;

public class OfferFilter {
    private OfferCategories category;
    private double latitude;
    private double longitude;
    private long radius; //In meters

    public OfferCategories getCategory() {
        return category;
    }

    public void setCategory(OfferCategories category) {
        this.category = category;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getRadius() {
        return radius;
    }

    public void setRadius(long radius) {
        this.radius = radius;
    }
}
