package at.ennui.backend.events.model;

import at.ennui.backend.events.configuration.EventCategories;
import org.springframework.beans.factory.annotation.Required;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EventFilter {
    private List<String> categories;
    private String startTime;
    private String endTime;

    private String country;
    private double longitude;
    private double latitude;
    private int radius;

    public HashSet<EventCategories> getCategories() {
        if(categories != null){
            HashSet<EventCategories> ecats = new HashSet<>();
            for(String cat: categories){
                if(cat.toLowerCase().contains("party")){
                    ecats.add(EventCategories.PARTY);
                }
                else if(cat.toLowerCase().contains("music")){
                    ecats.add(EventCategories.MUSIC);
                }
                else if(cat.toLowerCase().contains("art")){
                    ecats.add(EventCategories.ART);
                }
                else if(cat.toLowerCase().contains("game")){
                    ecats.add(EventCategories.GAMES);
                }
                else if(cat.toLowerCase().contains("food")){
                    ecats.add(EventCategories.FOOD);
                }
                else if(cat.toLowerCase().contains("comedy")){
                    ecats.add(EventCategories.COMEDY);
                }
                else if(cat.toLowerCase().contains("literatur")){
                    ecats.add(EventCategories.LITERATUR);
                }
                else if(cat.toLowerCase().contains("health")){
                    ecats.add(EventCategories.HEALTH);
                }
                else if(cat.toLowerCase().contains("shopping")){
                    ecats.add(EventCategories.SHOPPING);
                }
                else if(cat.toLowerCase().contains("home") || cat.toLowerCase().contains("garden")){
                    ecats.add(EventCategories.HOME_GARDEN);
                }
                else if(cat.toLowerCase().contains("sport")){
                    ecats.add(EventCategories.SPORT);
                }
                else if(cat.toLowerCase().contains("theatre")){
                    ecats.add(EventCategories.THEATRE);
                }
                else if(cat.toLowerCase().contains("other") || cat.toLowerCase().contains("sonstiges")){
                    ecats.add(EventCategories.OTHERS);
                }
            }
            return ecats;
        }
        return null;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public Date getStartTime() {
        if(startTime == null || startTime.equals("null")){
            return null;
        }
        try {
            return (new SimpleDateFormat("EE MMM dd yyyy HH:mm:ss zzzz", Locale.ENGLISH)).parse(startTime);
        } catch (ParseException e) {
            return null;
        }
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        if(startTime == null || startTime.equals("null")){
            return null;
        }
        try {
            return (new SimpleDateFormat("EE MMM dd yyyy HH:mm:ss zzzz", Locale.ENGLISH)).parse(endTime);
        } catch (ParseException e) {
            return null;
        }
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public boolean isValid(){
        return country != null && longitude != 0 && latitude != 0;
    }

    public boolean isValidTime(){
        if(getStartTime() != null && getEndTime() != null){
            return getStartTime().toInstant().isBefore(getEndTime().toInstant());
        }
        return false;
    }
}
