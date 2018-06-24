package at.ennui.backend.offers.api;

import at.ennui.backend.main.models.Holder;
import at.ennui.backend.offers.OfferService;
import at.ennui.backend.offers.configuration.OfferCategories;
import at.ennui.backend.offers.model.OfferDto;
import at.ennui.backend.offers.model.OfferFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Martin Singer on 19.09.2017.
 */
@RestController
@CrossOrigin
@RequestMapping("/offers")
public class OfferApi {
    private OfferService offerService;

    @RequestMapping(value = "/categories",method = RequestMethod.GET)
    public OfferCategories[] getOfferCategories(){
        return offerService.getOfferCategories();
    }

    @RequestMapping(value = "",method = RequestMethod.GET)
    public Holder<OfferDto> getOffersByCategory(OfferFilter offerFilter){
        return offerService.getOffersByCategory(offerFilter);
    }

    @RequestMapping(value = "/{ref}",method = RequestMethod.GET)
    public Holder<OfferDto> getOfferDetails(@PathVariable("ref") String reference){
        return offerService.getOfferDetailsByReference(reference);
    }

    @Autowired
    public void setOfferService(OfferService offerService){
        this.offerService = offerService;
    }
}
