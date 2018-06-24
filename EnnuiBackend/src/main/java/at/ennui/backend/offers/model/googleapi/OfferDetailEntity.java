package at.ennui.backend.offers.model.googleapi;

import java.util.HashMap;
import java.util.List;

public class OfferDetailEntity {
    private List<AddressComponent> address_components;
    private String formatted_address;
    private String international_phone_number;
    private HashMap<String,Object> geometry;
    private String name;
    private HashMap<String,Object> opening_hours;
    private List<GooglePhotoReference> photos;
    private Double rating;
    private List<Review> reviews;
    private String vicinity;
    private String website;

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public List<AddressComponent> getAddress_components() {
        return address_components;
    }

    public void setAddress_components(List<AddressComponent> address_components) {
        this.address_components = address_components;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getInternational_phone_number() {
        return international_phone_number;
    }

    public void setInternational_phone_number(String international_phone_number) {
        this.international_phone_number = international_phone_number;
    }

    public HashMap<String, Object> getGeometry() {
        return geometry;
    }

    public void setGeometry(HashMap<String, Object> geometry) {
        this.geometry = geometry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Object> getOpening_hours() {
        return opening_hours;
    }

    public void setOpening_hours(HashMap<String, Object> opening_hours) {
        this.opening_hours = opening_hours;
    }

    public List<GooglePhotoReference> getPhotos() {
        return photos;
    }

    public void setPhotos(List<GooglePhotoReference> photos) {
        this.photos = photos;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
