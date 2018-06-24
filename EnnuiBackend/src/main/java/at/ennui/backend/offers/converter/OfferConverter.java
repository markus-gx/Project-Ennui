package at.ennui.backend.offers.converter;

import at.ennui.backend.offers.model.LocationDetails;
import at.ennui.backend.offers.model.OfferDto;
import at.ennui.backend.offers.model.googleapi.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.stream.Location;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Martin Singer on 19.09.2017.
 */
@Component
public class OfferConverter {
    private ModelMapper modelMapper;

    public OfferDto convert(OfferDetailEntity entity){
        OfferDto dto = modelMapper.map(entity,OfferDto.class);
        LocationDetails locationDetails = new LocationDetails();
        locationDetails.setVicinity(entity.getVicinity());
        for(AddressComponent component : entity.getAddress_components()){
            if(component.getTypes().contains("street_number")){
                locationDetails.setStreet(locationDetails.getStreet() + " " + component.getLong_name());
            }
            if(component.getTypes().contains("route")){
                locationDetails.setStreet(component.getLong_name() + locationDetails.getStreet());
            }
            if(component.getTypes().contains("locality")){
                locationDetails.setCity(component.getLong_name());
            }
            if(component.getTypes().contains("country")){
                locationDetails.setCountry(component.getLong_name());
            }
            if(component.getTypes().contains("postal_code")){
                locationDetails.setZip(component.getLong_name());
            }
        }
        if(entity.getGeometry() != null && entity.getGeometry().get("location") != null){
            if(entity.getGeometry().get("location") instanceof LinkedHashMap){
                locationDetails.setLatitude((Double)((LinkedHashMap) entity.getGeometry().get("location")).get("lat"));
                locationDetails.setLongitude((Double)((LinkedHashMap) entity.getGeometry().get("location")).get("lng"));
            }
        }
        locationDetails.setFormatted_address(entity.getFormatted_address());
        dto.setLocationDetails(locationDetails);
        try {
            dto.setPeriods(modelMapper.map(entity.getOpening_hours().get("periods"),new TypeToken<List<TimePeriod>>() {}.getType()));
        }
        catch(NullPointerException e){
            TimePeriod p = new TimePeriod();
            p.setOpen(new TimePeriodDay(0,"Time not available"));
            p.setClose(new TimePeriodDay(0,"Time not available"));
        }
        if(entity.getOpening_hours() != null){
            if(entity.getOpening_hours().get("weekday_text") != null && entity.getOpening_hours().get("weekday_text") instanceof ArrayList){
                dto.setWeekday_text((ArrayList)entity.getOpening_hours().get("weekday_text"));
            }
            dto.setOpen_now((Boolean)entity.getOpening_hours().get("open_now"));
        }
        else{
            String text = "Not available!";
            List<String> lst = new ArrayList<>();
            lst.add(text);
            dto.setWeekday_text(lst);
            dto.setOpen_now(false);
        }

        return dto;
    }

    public List<OfferDto> convert(List<OffersEntity> entities){
        return entities.stream().map(this::convert).collect(Collectors.toList());
    }

    public OfferDto convert(OffersEntity entity){
        OfferDto dto = modelMapper.map(entity,OfferDto.class);
        dto.setLocationDetails(new LocationDetails());
        if(entity.getGeometry() != null && entity.getGeometry().get("location") != null){
            if(entity.getGeometry().get("location") instanceof LinkedHashMap){
                dto.getLocationDetails().setLatitude((Double)((LinkedHashMap) entity.getGeometry().get("location")).get("lat"));
                dto.getLocationDetails().setLongitude((Double)((LinkedHashMap) entity.getGeometry().get("location")).get("lng"));
            }
        }
        if(entity.getOpening_hours() != null && entity.getOpening_hours().get("open_now") != null){
            dto.setOpen_now((Boolean)entity.getOpening_hours().get("open_now"));
        }
        dto.getLocationDetails().setVicinity(entity.getVicinity());
        dto.setReference(entity.getReference());
        return dto;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
