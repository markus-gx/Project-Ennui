package at.ennui.backend.offers.model.googleapi;

import at.ennui.backend.offers.model.googleapi.OffersEntity;

import java.util.List;

public class OfferGoogleHolder {
    private List<OffersEntity> results;
    private String status;

    public List<OffersEntity> getResults() {
        return results;
    }

    public void setResults(List<OffersEntity> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
