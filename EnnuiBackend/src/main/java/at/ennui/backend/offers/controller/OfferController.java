package at.ennui.backend.offers.controller;

import at.ennui.backend.main.models.Holder;
import at.ennui.backend.offers.configuration.OfferCategories;
import at.ennui.backend.offers.converter.OfferConverter;
import at.ennui.backend.offers.model.OfferDto;
import at.ennui.backend.offers.model.OfferFilter;
import at.ennui.backend.offers.model.googleapi.OfferGoogleDetailHolder;
import at.ennui.backend.offers.model.googleapi.OfferGoogleHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin Singer on 19.09.2017.
 */
@Component
public class OfferController {
    private RestTemplate restTemplate;
    private OfferConverter offerConverter;
    private final String key = "AIzaSyAYM8yhGIpHw8sdctlX7bHCLje0spILKSg";

    public OfferCategories[] getOfferCategories(){
        return OfferCategories.values();
    }

    public Holder<OfferDto> getOffersByCategory(OfferFilter offerFilter){
        Holder<OfferDto> offerHolder = new Holder<OfferDto>();
        final long radius = offerFilter.getRadius() < 5000 ? 5000 : offerFilter.getRadius();
        OfferGoogleHolder holder = restTemplate.getForObject("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + offerFilter.getLatitude() + "," + offerFilter.getLongitude() + "&radius=" + radius + "&name=" + offerFilter.getCategory() + "&key=" + key,OfferGoogleHolder.class);
        offerHolder.setSuccess(true);
        offerHolder.setResult(offerConverter.convert(holder.getResults()));
        return offerHolder;
    }

    public Holder<OfferDto> getOfferDetailsByReference(String ref){
        Holder<OfferDto> offerHolder = new Holder<OfferDto>();
        OfferGoogleDetailHolder offerGoogleDetailHolder = restTemplate.getForObject("https://maps.googleapis.com/maps/api/place/details/json?reference=" + ref + "&key=" + key,OfferGoogleDetailHolder.class);
        if(offerGoogleDetailHolder.getResult() != null){
            List<OfferDto> list = new ArrayList<>();
            list.add(offerConverter.convert(offerGoogleDetailHolder.getResult()));
            offerHolder.setResult(list);
            offerHolder.setSuccess(true);
        }
        else{
            offerHolder.setSuccess(false);
            offerHolder.setMessage("Reference not found!");
        }
        return offerHolder;
    }

    @Autowired
    public void setOfferConverter(OfferConverter offerConverter) {
        this.offerConverter = offerConverter;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
