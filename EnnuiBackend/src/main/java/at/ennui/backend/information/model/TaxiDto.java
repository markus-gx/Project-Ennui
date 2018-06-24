package at.ennui.backend.information.model;

public class TaxiDto {
    private String name;
    private String icon;
    private String international_phone_number;
    private Object adress_components;
    private int rating;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getInternational_phone_number() {
        return international_phone_number;
    }

    public void setInternational_phone_number(String international_phone_number) {
        this.international_phone_number = international_phone_number;
    }

    public Object getAdress_components() {
        return adress_components;
    }

    public void setAdress_components(Object adress_components) {
        this.adress_components = adress_components;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
