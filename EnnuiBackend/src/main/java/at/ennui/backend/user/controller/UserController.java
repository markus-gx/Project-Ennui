package at.ennui.backend.user.controller;

import at.ennui.backend.crawling.CrawlingService;
import at.ennui.backend.events.EventService;
import at.ennui.backend.facebook.FacebookService;
import at.ennui.backend.games.GameService;
import at.ennui.backend.pages.PageService;
import at.ennui.backend.pages.model.PageEntity;
import at.ennui.backend.security.UserAuthentication;
import at.ennui.backend.user.converter.UserConverter;
import at.ennui.backend.user.model.UserDto;
import at.ennui.backend.user.model.UserEntity;
import at.ennui.backend.user.repository.UserRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.facebook.api.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.time.Clock;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class UserController {
    private EventService eventService;
    private GameService gameService;
    private UserRepository userRepository;
    private UserConverter userConverter;
    private PageService pageService;
    private FacebookService facebookService;
    private CrawlingService crawlingService;

    private Cache<String,UserEntity> loginUserCache = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.HOURS)
            .build();

    public long countAllUsers(){
        return userRepository.count();
    }

    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof UserAuthentication){
            UserEntity u = (UserEntity) authentication.getPrincipal();
            UserEntity loaded = userRepository.getUserEntityByFbId(u.getFbId());
            return loaded == null ? u : loaded;
        }
        else{
            return null;
        }
    }

    public String getUserToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof UserAuthentication){
            return (String) authentication.getCredentials();
        }
        else{
            return null;
        }
    }

    public User getFacebookProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof UserAuthentication){
            return (User) authentication.getDetails();
        }
        else{
            return null;
        }
    }

    public UserDto loginUser(){
        User fBUser = getFacebookProfile();
        final Thread[] t = {null};
        final String userToken = getUserToken();
        UserEntity user = loginUserCache.get(fBUser.getId(), k -> {
            t[0] = new Thread(() -> {
                List<PageEntity> pageEntities = pageService.getPagesFromUser(userToken);
                pageService.save(pageEntities);
                //crawlingService.crawlEventsFromPages(pageEntities);
            });
            return userRepository.getUserEntityByFbId(fBUser.getId());
        });
        if(user == null){ //Not saved yet
            user = getCurrentUser();
            user.setAdmin(false);
            user.setProfileImage(facebookService.getUserProfileImage(getUserToken(),user.getFbId()));
            loginUserCache.put(user.getFbId(),userRepository.save(user));
            eventService.addEvents(eventService.getEventsById(user.getFbId(),getUserToken()));
        }
        UserDto userDto = userConverter.convert(user);
        userDto.setFavouriteEvents(eventService.getFavorizedEventsByUser(user.getId()));
        userDto.setFavouriteGames(gameService.getFavorizedGamesByUser(user.getId()));
        if(t[0] != null){
            t[0].start();
        }
        return userDto;
    }

    @Autowired
    public void setGameService(GameService gameService) {
        this.gameService = gameService;
    }

    @Autowired
    public void setFacebookService(FacebookService facebookService){
        this.facebookService = facebookService;
    }

    @Autowired
    public void setPageService(PageService pageService){
        this.pageService = pageService;
    }

    @Autowired
    public void setUserConverter(UserConverter userConverter){
        this.userConverter = userConverter;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    @Autowired
    public void setCrawlingService(CrawlingService crawlingService){
        this.crawlingService = crawlingService;
    }
}
