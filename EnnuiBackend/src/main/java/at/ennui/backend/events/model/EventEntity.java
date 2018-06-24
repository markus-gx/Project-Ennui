package at.ennui.backend.events.model;

import at.ennui.backend.events.configuration.EventCategories;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "event")
public class EventEntity {
    @Id
    @GeneratedValue
    private long id;
    private long eventId;
    private Date endtime;
    private Date starttime;
    private String name;

    @Column(columnDefinition = "text(65535)")
    private String description;
    private String placeName;
    private String country;
    private String city;
    private Double latitude;
    private Double longitude;
    private String street;
    private String zip;
    private String ownerName;
    private Long ownerId;
    private String coverUrl;
    @Column(columnDefinition = "text(1000)")
    private String ticketUri;
    private boolean activated;
    private EventCategories category;

    public EventCategories getCategory() {
        return category;
    }

    public void setCategory(EventCategories category) {
        this.category = category;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getTicketUri() {
        return ticketUri;
    }

    public void setTicketUri(String ticketUri) {
        this.ticketUri = ticketUri;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
