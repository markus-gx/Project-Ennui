package at.ennui.backend.facebook.controller;

import at.ennui.backend.events.model.EventDto;
import at.ennui.backend.events.model.EventEntity;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.social.facebook.api.*;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class FacebookController {
    private Cache<String,User> userCache = Caffeine.newBuilder()
            .expireAfterWrite(3500, TimeUnit.SECONDS)
            .maximumSize(1500)
            .build();

    public String getAppAccessToken(){
        return "967640606679290|aCgl43Dq0ELePxHtRwxmmX3iAHM";
    }

    public User getFacebookUserFromToken(String token){
        return userCache.get(token,key -> {
            Facebook facebook = new FacebookTemplate(token,"atennui");
            return facebook.userOperations().getUserProfile();
        });
    }

    public String getProfileImageByToken(String token,String id){
        Facebook facebook = new FacebookTemplate(token,"atennui");
        Map data = facebook.restOperations().getForObject("https://graph.facebook.com/" + id + "/picture?redirect=0&access_token=" + token,Map.class);
        if(data != null){
            return (data.get("data")) != null ? (String)((Map) data.get("data")).get("url") : "";
        }
        return "";
    }
}
