package at.ennui.backend.events.converter;

import at.ennui.backend.events.model.EventDto;
import at.ennui.backend.events.model.EventEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.Event;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class EventConverter {
    private ModelMapper modelMapper;

    public List<EventDto> convert(List<EventEntity> entities){
        return entities.stream().map(this::convert).collect(Collectors.toList());
    }

    public EventEntity convert(Object event){
        EventEntity eventEntity = modelMapper.map(event,EventEntity.class);
        if(event instanceof Map){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            try {
                eventEntity.setStarttime(format.parse((String)((Map) event).get("start_time")));
            } catch (Exception e) {
               eventEntity.setStarttime(null);
            }
            try {
                eventEntity.setEndtime(format.parse((String)((Map) event).get("end_time")));
            } catch (Exception e) {
                eventEntity.setEndtime(null);
            }
            eventEntity.setEventId(eventEntity.getId());
            eventEntity.setId(0);
            if(((Map) event).get("place") != null){
                Map place = (Map) ((Map) event).get("place");
                if(place.get("location") != null){
                    Map location = (Map) place.get("location");
                    if(location.get("latitude") instanceof Integer){
                        eventEntity.setLatitude(((Integer) location.get("latitude")).doubleValue());
                    }
                    else if(location.get("latitude") instanceof Double){
                        eventEntity.setLatitude((Double) location.get("latitude"));
                    }
                    if(location.get("longitude") instanceof Integer){
                        eventEntity.setLongitude(((Integer) location.get("longitude")).doubleValue());
                    }
                    else if(location.get("longitude") instanceof Double){
                        eventEntity.setLongitude((Double) location.get("longitude"));
                    }
                    eventEntity.setCountry(convertCountryNameToCountryCode((String) location.get("country")));
                    eventEntity.setCity((String) location.get("city"));
                    eventEntity.setStreet((String) location.get("street"));
                    eventEntity.setZip((String) location.get("zip"));
                }
            }
            if(((Map) event).get("cover") != null){
                Map cover = (Map) ((Map) event).get("cover");
                eventEntity.setCoverUrl((String)cover.get("source"));
            }
            eventEntity.setTicketUri((String)((Map) event).get("ticket_uri"));
        }
        return eventEntity;
    }

    public EventDto convert(EventEntity entity){
        EventDto d =  modelMapper.map(entity,EventDto.class);
        return d;
    }

    public EventEntity convert(Event event){
        EventEntity e = modelMapper.map(event,EventEntity.class);
        e.setId(0);
        e.setOwnerId(null);
        if(event.getPlace() != null && event.getPlace().getLocation() != null){
            e.setCountry(convertCountryNameToCountryCode(event.getPlace().getLocation().getCountry()));
            e.setCity(event.getPlace().getLocation().getCity());
            e.setLongitude(event.getPlace().getLocation().getLongitude());
            e.setLatitude(event.getPlace().getLocation().getLatitude());
            e.setStreet(event.getPlace().getLocation().getStreet());
            e.setZip(event.getPlace().getLocation().getZip());
        }
        e.setCoverUrl(event.getCover() != null ? event.getCover().getSource() : "");
        return e;
    }

    public String convertCountryNameToCountryCode(String countryName){
        Map<String, String> countries = new HashMap<>();
        for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
            if(l.getDisplayCountry(Locale.ENGLISH).equals(countryName)){
                return iso;
            }
        }
        return "";
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }
}
