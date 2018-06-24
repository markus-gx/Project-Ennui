package at.ennui.backend.offers;

import at.ennui.backend.main.models.Holder;
import at.ennui.backend.offers.configuration.OfferCategories;
import at.ennui.backend.offers.controller.OfferController;
import at.ennui.backend.offers.model.OfferDto;
import at.ennui.backend.offers.model.OfferFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Martin Singer on 19.09.2017.
 */
@Component
public class OfferService {
    private OfferController offerController;

    public OfferCategories[] getOfferCategories(){
        return offerController.getOfferCategories();
    }

    public Holder<OfferDto> getOffersByCategory(OfferFilter offerFilter){
        if(offerFilter.getLongitude() != 0 && offerFilter.getLatitude() != 0) {
            return offerController.getOffersByCategory(offerFilter);
        }
        return new Holder<OfferDto>(false,"Something went wrong");
    }

    public Holder<OfferDto> getOfferDetailsByReference(String ref){
        if(ref != null && !ref.isEmpty()){
            return offerController.getOfferDetailsByReference(ref);
        }
        return new Holder<OfferDto>(false,"Invalid reference id!");
    }

    @Autowired
    public void setOfferController(OfferController offerController) {
        this.offerController = offerController;
    }
}
