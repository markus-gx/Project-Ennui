package at.ennui.backend.offers.model.googleapi;

import java.util.List;

public class OfferGoogleDetailHolder {
    private String status;
    private OfferDetailEntity result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OfferDetailEntity getResult() {
        return result;
    }

    public void setResult(OfferDetailEntity result) {
        this.result = result;
    }
}
