package at.ennui.backend.information.controller;

import at.ennui.backend.events.EventService;
import at.ennui.backend.information.model.*;
import at.ennui.backend.user.UserService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class InformationController {
    private UserService userService;
    private EventService eventService;

    private Cache<Integer,StatisticsDto> statisticsCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    public Object getTaxis(double latitude, double longitude){
        RestTemplate template = new RestTemplate();
        template.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=25000&name=taxi&key=AIzaSyAE0jVybaY4jzT3irMM9g8THf3qGF6V9xE";
        PlaceHolder holder = template.getForObject(url,PlaceHolder.class);
        List<PlaceDTO> places = holder.getResults();
        List<TaxiDto> taxis = new ArrayList<>();
        places.forEach(placeDTO -> {
            String url2 = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+placeDTO.getPlace_id() +"&key=AIzaSyAE0jVybaY4jzT3irMM9g8THf3qGF6V9xE";
            TaxiHolder taxiHolder = template.getForObject(url2,TaxiHolder.class);

            taxis.add(taxiHolder.getResult());
        });
        return taxis;
    }

    public StatisticsDto getStatistics(){
        return statisticsCache.get(0, k-> {
            StatisticsDto statisticsDto = new StatisticsDto();
            statisticsDto.setUsers(userService.countAllUsers());
            statisticsDto.setEvents(eventService.countAllEvents());
            statisticsDto.setCountryWithEvents(eventService.getCountriesWithEvents());
            return statisticsDto;
        });
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }
}
