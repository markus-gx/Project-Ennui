package at.ennui.backend.facebook;

import at.ennui.backend.facebook.controller.FacebookController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.Invitation;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.User;
import org.springframework.stereotype.Component;

@Component
public class FacebookService {
    private FacebookController facebookController;

    public User getFacebookUserByToken(String token){
        return facebookController.getFacebookUserFromToken(token);
    }

    public String getUserProfileImage(String token, String id){
        return facebookController.getProfileImageByToken(token, id);
    }

    public String getAppAccessToken(){
        return facebookController.getAppAccessToken();
    }

    @Autowired
    public void setFacebookController(FacebookController facebookController) {
        this.facebookController = facebookController;
    }
}
