package at.ennui.backend.crawling.controller;

import at.ennui.backend.events.EventService;
import org.springframework.web.client.RestTemplate;

public class EventCrawlTask implements Runnable {
    private final String id;
    private EventService eventService;
    private final String token;
    public EventCrawlTask(String i, EventService eventService,String fbToken){
        this.id = i;
        this.eventService = eventService;
        this.token = fbToken;
    }

    @Override
    public void run() {
        eventService.addEvents(eventService.getEventsById(id,token));
    }
}
