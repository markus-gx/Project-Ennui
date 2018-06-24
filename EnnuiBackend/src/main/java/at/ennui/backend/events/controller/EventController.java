package at.ennui.backend.events.controller;

import at.ennui.backend.algorithms.AlgorithmService;
import at.ennui.backend.algorithms.model.AlgorithmDTO;
import at.ennui.backend.events.configuration.EventCategories;
import at.ennui.backend.events.converter.EventConverter;
import at.ennui.backend.events.model.*;
import at.ennui.backend.events.repository.EventRepository;
import at.ennui.backend.events.repository.FavorizedEventsRepository;
import at.ennui.backend.events.repository.UserClicksRepository;
import at.ennui.backend.main.models.Holder;
import at.ennui.backend.user.UserService;
import at.ennui.backend.user.model.UserEntity;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Component;
import weka.core.Instances;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class EventController {
    private UserService userService;
    private EventRepository eventRepository;
    private FavorizedEventsRepository favorizedEventsRepository;
    private UserClicksRepository userClicksRepository;
    private EventConverter eventConverter;
    private AlgorithmService algorithmService = new AlgorithmService();

    public Holder<EventDto> editEvent(EventEntity newEntity){
        UserEntity user = userService.getCurrentUser();
        EventEntity eventEntity = eventRepository.findOne(newEntity.getId());
        if(user.isAdmin() || user.getId().equals(newEntity.getOwnerId())){
            if(newEntity.getStarttime().after(newEntity.getEndtime())){
                return new Holder<>(false,"Starttime is after endtime!");
            }
            newEntity.setId(eventEntity.getId());
            newEntity.setOwnerId(user.getId());
            newEntity.setOwnerName(user.getName());
            eventRepository.save(newEntity);
                return new Holder<>(true,"Updated!");
        }
        return new Holder<>(false,"You do not have permission to perform this command!");
    }

    public Holder<EventDto> getUserSubmissions(){
        UserEntity u = userService.getCurrentUser();
        if(u != null){
            List<EventEntity> submissions = eventRepository.findByOwnerId(u.getId());
            Holder<EventDto> e = new Holder<EventDto>(true,eventConverter.convert(submissions));
            e.setMessage("submissions");
            return e;
        }
        return new Holder<EventDto>(false,"User not found!");
    }

    public Object getCountriesWithEvents(){
        return eventRepository.getCountriesWithEventCount();
    }

    public long countAllEvents(){
        return eventRepository.count();
    }

    public Holder<EventDto> getNearbyEvents(EventFilter eventFilter) throws Exception {
        Holder<EventDto> eventDtoHolder = new Holder<EventDto>();
        List<EventEntity> entities = eventRepository.getEntitiesByRadius(eventFilter.getRadius(),eventFilter.getLongitude(),eventFilter.getLatitude());
        if(eventFilter.getCategories() != null && eventFilter.getCategories().size() > 0){
            entities = entities.stream().filter(e -> eventFilter.getCategories().contains(e.getCategory())).collect(Collectors.toList());
        }
        if(eventFilter.isValidTime()){
            entities = entities.stream()
                    .filter(e -> getTryInstant(e.getStarttime()).isAfter(eventFilter.getStartTime().toInstant())
                            && getTryInstant(e.getEndtime()).isBefore(eventFilter.getEndTime().toInstant())
                            && getTryInstant(e.getStarttime()).isBefore(eventFilter.getEndTime().toInstant()))
                    .collect(Collectors.toList());
        }

        if(entities != null && entities.size() > 0){
            eventDtoHolder.setResult(eventConverter.convert(entities));
            eventDtoHolder.setSuccess(true);
        }
        //Recommendation Algorithm
        UserEntity user = userService.getCurrentUser();
        if(user != null){
            List<UserClickEntity> clicks = userClicksRepository.findByUserId(user.getId());
            List<Long> ids = clicks.stream().map(UserClickEntity::getEventId).collect(Collectors.toList());
            List<EventEntity> trainingsSet = eventRepository.findByIdIn(ids); //This list is the main trainings set
            Map<Long,EventEntity> plainEvents = trainingsSet.stream().collect(Collectors.toMap(EventEntity::getId, Function.identity()));
            //DUplication
            for(UserClickEntity click : clicks){
                if(click.getCount() >= 2){
                    for(int i = 0; i < click.getCount()-1; i++){
                        EventEntity e = plainEvents.get(click.getEventId());
                        trainingsSet.add(e);
                    }
                }
            }

            eventDtoHolder.setRecommendedResults(getRecommendations(trainingsSet,entities));
        }
        return eventDtoHolder;
    }

    private Instant getTryInstant(Date date){
        if(date != null){
            return date.toInstant();
        }
        else{
            return new Date().toInstant();
        }
    }

    public void userClickedEvent(long eventId,long count){
        UserEntity u = userService.getCurrentUser();
        if(u != null){
            UserClickEntity userClickEntity = userClicksRepository.findByUserIdAndEventId(u.getId(),eventId);
            if(userClickEntity != null){
                userClickEntity.setCount(userClickEntity.getCount()+ count);
            }
            else{
                userClickEntity = new UserClickEntity();
                userClickEntity.setEventId(eventId);
                userClickEntity.setUserId(u.getId());
                userClickEntity.setCount(count);
            }
            userClicksRepository.save(userClickEntity);
        }
    }

    public List<EventDto> getFavorizedEventsByUser(Long userId){
        List<Long> ids = favorizedEventsRepository.findEventIdByUserId(userId);
        return eventConverter.convert(eventRepository.findByIdIn(ids));
    }

    public Holder<EventDto> unFavorizeEvent(Long eventId){
        UserEntity u = userService.getCurrentUser();
        if(u != null){
            FavorizedEventsEntity e = favorizedEventsRepository.findByUserIdAndEventId(u.getId(),eventId);
            if( e != null){
                favorizedEventsRepository.delete(e.getId());
                return new Holder<EventDto>(true);
            }
            else return new Holder<EventDto>(false,"Mapping not found!");
        }
        return new Holder<EventDto>(false);
    }

    public Holder<EventDto> favorizeEvent(Long eventId){
        UserEntity u = userService.getCurrentUser();
        if(u != null){
            FavorizedEventsEntity entity = new FavorizedEventsEntity();
            if(favorizedEventsRepository.findByUserIdAndEventId(u.getId(),eventId) == null) {
                if(eventRepository.findOne(eventId) != null){
                    entity.setEventId(eventId);
                    entity.setUserId(u.getId());
                    favorizedEventsRepository.save(entity);
                    return new Holder<EventDto>(true);
                }
                else return new Holder<EventDto>(false,"Event not found!");
            }
            else return new Holder<EventDto>(true,"Already favored!");
        }
        return new Holder<EventDto>(false);
    }

    public List<EventEntity> getEventsById(String id, String token){
        Facebook facebook = new FacebookTemplate(token,"atennui");
        List<EventEntity> entities = new ArrayList<>();
        List events;
        String after = "";
        do{
            Map data = facebook.restOperations().getForObject("https://graph.facebook.com/" + id + "/events?fields=id,end_time,start_time,name,description,place,owner,cover,ticket_uri&since=" + System.currentTimeMillis() / 1000L + "&access_token=" + token + "&after=" + after,Map.class);
            events = (ArrayList) data.get("data");
            after = getAfterKey((Map) data.get("paging"));
            for(Object m : events){
                EventEntity e = eventConverter.convert(m);
                e.setCategory(calculateEventCategory(e));
                entities.add(e);
            }
        }while(events.size() > 0);
        return entities;
    }

    public Holder<EventDto> getEventsByPlace(double latitude, double longitude){
        List<EventEntity> e = eventRepository.findByLatitudeAndLongitude(latitude,longitude);
        return new Holder<EventDto>(true,eventConverter.convert(e));
    }

    private String getAfterKey(Map paging){
        if(paging != null){
            Map cursors = (Map) paging.get("cursors");
            if(cursors != null){
                return (String) cursors.get("after");
            }
        }
        return null;
    }

    public Holder<EventDto> getNotActivatedEvents(){
        Holder<EventDto> eventDtoHolder = new Holder<EventDto>();
        eventDtoHolder.setSuccess(false);
        if(userService.getCurrentUser().isAdmin()){
            List<EventEntity> eventEntities = eventRepository.getNotActivatedEvents();
            eventDtoHolder.setResult(eventConverter.convert(eventEntities));
            eventDtoHolder.setSuccess(true);
            eventDtoHolder.setMessage("events");
            return eventDtoHolder;
        }
        eventDtoHolder.setMessage("You do not have permission to perform this command!");
        return eventDtoHolder;
    }

    public Holder<EventDto> activateEvent(long eventId){
        Holder<EventDto> eventDtoHolder = new Holder<EventDto>();
        eventDtoHolder.setSuccess(false);
        if(userService.getCurrentUser().isAdmin()){
            EventEntity e = eventRepository.findOne(eventId);
            e.setActivated(true);
            List<EventDto> list = new ArrayList<>();
            list.add(eventConverter.convert(eventRepository.save(e)));
            eventDtoHolder.setResult(list);
            return eventDtoHolder;
        }
        return eventDtoHolder;
    }

    public void deleteEvent(long eventId){
        if(userService.getCurrentUser().isAdmin()) {
            eventRepository.delete(eventId);
            favorizedEventsRepository.deleteByEventId(eventId);
        }
    }

    public EventEntity saveEvent(EventEntity eventDto){
        if(eventDto.getName() != null && eventDto.getCity() != null && eventDto.getCountry() != null && eventDto.getEndtime() != null
                && eventDto.getStarttime() != null && eventDto.getLatitude() != null && eventDto.getLongitude() != null){
            return eventRepository.save(eventDto);
        }
        return null;
    }

    public synchronized Holder<EventDto> addEvents(List<EventEntity> entities){
        List<EventEntity> eventEntities = new ArrayList<>();
        for(EventEntity eventEntity : entities){
            eventEntities.add(addEventWithFbCheck(eventEntity));
        }
        notifyAll();
        return new Holder<EventDto>(true,eventConverter.convert(eventEntities.stream().filter(Objects::nonNull).collect(Collectors.toList())));
    }

    public EventEntity addEventWithFbCheck(EventEntity entity){
        try{
            if(eventRepository.getEntityByFbId(entity.getEventId()) == null){
                entity.setActivated(true);
                return eventRepository.save(entity);
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    public Holder<EventDto> addEventForActivation(EventEntity entity) throws NotFoundException {
        UserEntity userEntity = userService.getCurrentUser();
        if(userEntity != null && userEntity.getId() != null){
            entity.setOwnerId(userEntity.getId());
            entity.setOwnerName(userEntity.getName());
            entity.setActivated(false);
            List<EventDto> saved = new ArrayList<>();
            EventDto sdto = eventConverter.convert(saveEvent(entity));
            if(sdto != null){
                saved.add(sdto);
                return new Holder<EventDto>(true,saved);
            }
        }
        return new Holder<EventDto>(false);
    }

    private double degreeToRadiant(double deg) {
        return deg * (Math.PI/180);
    }

    private double getDistanceFromLatLonInKm(double lat1,double lon1,double lat2,double lon2) {
        int R = 6371; //Earth Radius
        double dLat = degreeToRadiant(lat2-lat1);
        double dLon = degreeToRadiant(lon2-lon1);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(degreeToRadiant(lat1)) * Math.cos(degreeToRadiant(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c; //Distance in km
    }
    private EventCategories calculateEventCategory(EventEntity e){
        List<EventCategories> categories = new ArrayList<>();
        if(e.getName() != null){
            String wholeText = e.getName().toLowerCase() + " " + e.getOwnerName().toLowerCase();
            if(e.getDescription() != null){
                wholeText = wholeText + " " + e.getDescription().toLowerCase();
            }
            if(e.getPlaceName() != null){
                wholeText = wholeText+ " " + e.getPlaceName().toLowerCase();
            }
            HashMap<String,EventCategories> keywords = new HashMap<>();
            keywords.put(".*party.*",EventCategories.PARTY);
            //keywords.put(".*nacht.*",EventCategories.PARTY);
            //keywords.put(".*night.*",EventCategories.PARTY);
            keywords.put(".*schankgetränke.*",EventCategories.PARTY);
            keywords.put(".*feier.*",EventCategories.PARTY);
            keywords.put(".*dj.*",EventCategories.PARTY);
            keywords.put("dj.*",EventCategories.PARTY);
            keywords.put(".*deejay.*",EventCategories.PARTY);
            keywords.put(".*festival.*",EventCategories.PARTY);
            keywords.put(".*eskalier.*",EventCategories.PARTY);
            keywords.put(" house ",EventCategories.PARTY);
            keywords.put(" club ", EventCategories.PARTY);
            keywords.put(" mausefalle ", EventCategories.PARTY);

            keywords.put(".*festival.*",EventCategories.MUSIC);
            keywords.put(".*[ck]on[cz]ert.*",EventCategories.MUSIC);
            keywords.put(".*musi[ck].*",EventCategories.MUSIC);
            keywords.put(".*band.*",EventCategories.MUSIC);
            keywords.put(".*album.*",EventCategories.MUSIC);
            keywords.put(".*tour ",EventCategories.MUSIC);
            keywords.put(".*lieder.*",EventCategories.MUSIC);
            keywords.put(".*rock.*",EventCategories.MUSIC);
            keywords.put(".*folk.*",EventCategories.MUSIC);
            keywords.put(".*pop.*",EventCategories.MUSIC);
            keywords.put(".*song.*",EventCategories.MUSIC);
            keywords.put(" pian.*",EventCategories.MUSIC);


            keywords.put(".*kunst.*",EventCategories.ART);
            keywords.put(" art ",EventCategories.ART);
            keywords.put(".*galerie.*",EventCategories.ART);
            keywords.put(".*ausstellung.*",EventCategories.ART);
            keywords.put(".*ballet.*",EventCategories.ART);


            keywords.put(".*turnier.*",EventCategories.GAMES);
            keywords.put(" fc ",EventCategories.GAMES);

            keywords.put(".*food.*",EventCategories.FOOD);
            keywords.put(" essen ",EventCategories.FOOD);
            keywords.put(".*koch.*",EventCategories.FOOD);
            keywords.put(" wirtshaus ",EventCategories.FOOD);
            keywords.put(" gasthaus ",EventCategories.FOOD);
            keywords.put(" restaurant ",EventCategories.FOOD);
            keywords.put(" ristorante ",EventCategories.FOOD);
            keywords.put(" diner ",EventCategories.FOOD);

            keywords.put(".*comedian.*",EventCategories.COMEDY);
            keywords.put(".*gags.*",EventCategories.COMEDY);
            keywords.put(".*comedy.*",EventCategories.COMEDY);
            keywords.put(".*kabarett.*",EventCategories.COMEDY);
            keywords.put(".*humor.*",EventCategories.COMEDY);
            keywords.put(".*witzig.*",EventCategories.COMEDY);

            keywords.put(".*lesung.*",EventCategories.LITERATUR);
            keywords.put(".*autor.*",EventCategories.LITERATUR);
            keywords.put(".*buch.*",EventCategories.LITERATUR);
            keywords.put(" book ",EventCategories.LITERATUR);
            keywords.put("roman ",EventCategories.LITERATUR);
            keywords.put(".*verlag ",EventCategories.LITERATUR);

            keywords.put(".*fitness.*",EventCategories.HEALTH);
            keywords.put(".*health.*",EventCategories.HEALTH);
            keywords.put(".*ernährung.*",EventCategories.HEALTH);
            keywords.put(".*gesundheit ",EventCategories.HEALTH);

            keywords.put(".*shopping.*",EventCategories.SHOPPING);
            keywords.put(".*einkaufen.*",EventCategories.SHOPPING);
            keywords.put(".*flohmarkt ",EventCategories.SHOPPING);

            keywords.put(" garten ",EventCategories.HOME_GARDEN);
            keywords.put(" gärtner ",EventCategories.HOME_GARDEN);
            keywords.put(" haus ",EventCategories.HOME_GARDEN);
            keywords.put(" wohnen ",EventCategories.HOME_GARDEN);
            keywords.put(" heimwerk.*",EventCategories.HOME_GARDEN);

            keywords.put(".*sport ",EventCategories.SPORT);
            keywords.put(".?fc ",EventCategories.SPORT);
            keywords.put(".?union ",EventCategories.SPORT);
            keywords.put(".?askö ",EventCategories.SPORT);
            keywords.put(".*formel 1.*",EventCategories.SPORT);
            keywords.put(".?ehc ",EventCategories.SPORT);
            keywords.put(".?sk ",EventCategories.SPORT);
            keywords.put(".?sv ",EventCategories.SPORT);
            keywords.put(".?sc ",EventCategories.SPORT);
            keywords.put(".*arena.*",EventCategories.SPORT);
            keywords.put(".*stadion.*",EventCategories.SPORT);
            keywords.put(".*eishalle.*",EventCategories.SPORT);
            keywords.put(".*stadium.*",EventCategories.SPORT);
            keywords.put(".?sportplatz ",EventCategories.SPORT);
            keywords.put(" marathon ",EventCategories.SPORT);
            keywords.put(" lauf ",EventCategories.SPORT);
            keywords.put(" eishockey ", EventCategories.SPORT);

            keywords.put(".*theater.*",EventCategories.THEATRE);
            keywords.put(".*musical.*",EventCategories.THEATRE);
            keywords.put(".*choreo.*",EventCategories.THEATRE);

            String finalWholeText = wholeText;
            keywords.forEach((k, v) -> {
                Pattern p = Pattern.compile(k);
                Matcher m = p.matcher(finalWholeText);

                if(m.find()){

                    categories.add(v);
                }
            });
        }
        if(categories.size() <= 0){
            categories.add(EventCategories.OTHERS);
        }
        Map<EventCategories, Long> counts = categories.stream().collect(Collectors.groupingBy(c -> c, Collectors.counting()));
        return counts.keySet().stream().findFirst().get();
    }

    @Autowired
    public void setUserClicksRepository(UserClicksRepository userClicksRepository){
        this.userClicksRepository = userClicksRepository;
    }

    @Autowired
    public void setFavorizedEventsRepository(FavorizedEventsRepository favorizedEventsRepository){
        this.favorizedEventsRepository = favorizedEventsRepository;
    }

    @Autowired
    public void setEventRepository(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    @Autowired
    public void setEventConverter(EventConverter eventConverter){
        this.eventConverter = eventConverter;
    }

    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    public List<EventDto> getRecommendations(List<EventEntity> trainingsSet, List<EventEntity> events) throws Exception {
        UserEntity u = userService.getCurrentUser();
        if(u != null){

            List<AlgorithmDTO> data = algorithmService.convertEventData(trainingsSet);
            Instances trainingSet =  algorithmService.prepareTrainingSet(data);
            List<EventEntity> allEvents = events;
            List<AlgorithmDTO> eventsConverted = algorithmService.convertEventData(allEvents);
            List<Long> results = algorithmService.applyCART(eventsConverted,trainingSet);
            List<EventDto> recommendedEvents  = eventConverter.convert(eventRepository.findByIdIn(results));
            recommendedEvents.sort(new Comparator<EventDto>() {
                @Override
                public int compare(EventDto o1, EventDto o2) {
                    return o1.getStarttime().compareTo(o2.getStarttime());
                }
            });
            return recommendedEvents.stream().limit(10).collect(Collectors.toList());

        }
        return null;
    }

}
