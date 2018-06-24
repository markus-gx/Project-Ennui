package at.ennui.backend.offers.model.googleapi;

import java.util.HashMap;

/**
 * Created by Martin Singer on 19.09.2017.
 */
public class OffersEntity {
    private HashMap<String,Object> geometry;
    private String name;
    private HashMap<String,Object> opening_hours;
    private Double rating;
    private String vicinity;
    private String place_id;
    private String id;
    private String reference;

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

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
