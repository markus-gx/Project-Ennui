package at.ennui.backend.events.api;

import at.ennui.backend.events.EventService;
import at.ennui.backend.events.model.EventDto;
import at.ennui.backend.events.model.EventEntity;
import at.ennui.backend.events.model.EventFilter;
import at.ennui.backend.main.models.Holder;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/events")
@CrossOrigin
public class EventApi {
    private EventService eventService;

    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    public Holder<EventDto> editEvent(@RequestBody EventEntity e){
        return eventService.editEvent(e);
    }

    @RequestMapping(value = "",method = RequestMethod.GET)
    public Holder<EventDto> getEvents(EventFilter eventFilter) throws Exception {
       return eventService.getNearbyEvents(eventFilter);
    }

    @RequestMapping(value = "/logged",method = RequestMethod.GET)
    public Holder<EventDto> getEventsWhenLoggedIn(EventFilter eventFilter) throws Exception {
        return eventService.getNearbyEvents(eventFilter);
    }

    @RequestMapping(value = "/notActivated",method = RequestMethod.GET)
    public Holder<EventDto> getNotActivatedEvents(){
        return eventService.getNotActivatedEvents();
    }

    @RequestMapping(value = "/activate/{id}",method = RequestMethod.POST)
    public Holder<EventDto> activateEvent(@PathVariable("id") long eventId){
        return eventService.activateEvent(eventId);
    }

    @RequestMapping(value = "/delete/{id}",method = RequestMethod.POST)
    public void deleteEvent(@PathVariable("id") long eventId){
        eventService.deleteEvent(eventId);
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public Holder<EventDto> addEvent(@RequestBody EventEntity entity) throws NotFoundException {
        return eventService.addEvent(entity);
    }

    @RequestMapping(value = "/favorize/{id}",method = RequestMethod.POST)
    public Holder<EventDto> favorizeEvent(@PathVariable("id") Long eventId){
        eventService.userClickedEvent(eventId,10);
        return eventService.favorizeEvent(eventId);
    }

    @RequestMapping(value = "/unfavorize/{id}",method = RequestMethod.POST)
    public Holder<EventDto> unFavorizeEvent(@PathVariable("id") Long eventId){
        eventService.userClickedEvent(eventId,-10);
        return eventService.unFavorizeEvent(eventId);
    }




    @RequestMapping(value = "/mysubmissions",method = RequestMethod.GET)
    public Holder<EventDto> getUserSubmissions(){
        return eventService.getUserSubmissions();
    }

    @RequestMapping(value = "/clicked/{id}",method = RequestMethod.POST)
    public void userClickedEvent(@PathVariable("id") Long eventId){
        eventService.userClickedEvent(eventId,1);
    }

    @RequestMapping(value = "/place",method = RequestMethod.GET)
    public Holder<EventDto> getEventsByPlace(EventFilter eventFilter){
        return eventService.getEventsByPlace(eventFilter.getLatitude(),eventFilter.getLongitude());
    }

    @Autowired
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }
}
