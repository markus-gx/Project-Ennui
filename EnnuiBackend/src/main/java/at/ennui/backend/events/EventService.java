package at.ennui.backend.events;

import at.ennui.backend.events.controller.EventController;
import at.ennui.backend.events.exception.EventException;
import at.ennui.backend.events.exception.EventFilterException;
import at.ennui.backend.events.model.EventDto;
import at.ennui.backend.events.model.EventEntity;
import at.ennui.backend.events.model.EventFilter;
import at.ennui.backend.main.models.Holder;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class EventService {
    private EventController eventController;

    public Holder<EventDto> editEvent(EventEntity eventEntity){
        return eventController.editEvent(eventEntity);
    }

    public Holder<EventDto> getUserSubmissions(){
        return eventController.getUserSubmissions();
    }

    public Object getCountriesWithEvents(){
        return eventController.getCountriesWithEvents();
    }

    public long countAllEvents(){
        return eventController.countAllEvents();
    }

    public Holder<EventDto> getNearbyEvents(EventFilter eventFilter) throws Exception {
        if(eventFilter.isValid()){
            return eventController.getNearbyEvents(eventFilter);
        }
        else{
            throw new EventFilterException("Some fields are missing!");
        }
    }

    public void userClickedEvent(long eventId, long count){
        eventController.userClickedEvent(eventId, count);
    }

    public Holder<EventDto> activateEvent(long eventId){
        return eventController.activateEvent(eventId);
    }

    public Holder<EventDto> getNotActivatedEvents(){
        return eventController.getNotActivatedEvents();
    }

    public void deleteEvent(long eventId){
        eventController.deleteEvent(eventId);
    }

    public Holder<EventDto> addEvent(EventEntity entity) throws NotFoundException {
        if(entity != null){
            return eventController.addEventForActivation(entity);
        }
        else{
            throw new EventException("Request-Body was empty!");
        }
    }

    public Holder<EventDto> addEvents(List<EventEntity> entities){
        if(entities != null){
            return eventController.addEvents(entities);
        }
        return new Holder<EventDto>(false);
    }

    public Holder<EventDto> favorizeEvent(Long eventId){
        if(eventId != null){
            return eventController.favorizeEvent(eventId);
        }
        return new Holder<EventDto>(false);
    }

    public Holder<EventDto> unFavorizeEvent(Long eventId){
        if(eventId != null){
            return eventController.unFavorizeEvent(eventId);
        }
        return new Holder<EventDto>(false);
    }

    public List<EventDto> getFavorizedEventsByUser(Long userId){
        if(userId != null){
            return eventController.getFavorizedEventsByUser(userId);
        }
        return null;
    }

    public Holder<EventDto> getEventsByPlace(double latitude, double longitude){
        return eventController.getEventsByPlace(latitude,longitude);
    }

    public List<EventEntity> getEventsById(String id, String token){
        List<EventEntity> eventEntities = eventController.getEventsById(id, token);
        return eventEntities;
    }

    @Autowired
    public void setEventController(EventController eventController){
        this.eventController = eventController;
    }


}
