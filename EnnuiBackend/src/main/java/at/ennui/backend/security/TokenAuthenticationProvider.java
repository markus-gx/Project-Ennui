package at.ennui.backend.security;

import at.ennui.backend.facebook.FacebookService;
import at.ennui.backend.facebook.exception.FacebookAuthenticationException;
import at.ennui.backend.facebook.converter.FacebookConverter;
import at.ennui.backend.user.model.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.social.ExpiredAuthorizationException;
import org.springframework.social.InvalidAuthorizationException;
import org.springframework.social.facebook.api.User;
import org.springframework.stereotype.Component;

@Component
public class TokenAuthenticationProvider implements AuthenticationProvider{
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenAuthenticationProvider.class);
    private UserAuthentication userAuthentication;
    private FacebookService facebookService;
    private FacebookConverter facebookConverter;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(!(authentication instanceof UserAuthentication)){
            return null;
        }
        UserEntity userEntity = null;
        User facebookUser = null;
        final String token = (String) authentication.getCredentials();
        try{
            facebookUser = facebookService.getFacebookUserByToken(token);
            userEntity = facebookConverter.convertFacebookUserToUserDto(facebookUser);
        }
        catch(InvalidAuthorizationException | ExpiredAuthorizationException e){
            throw new FacebookAuthenticationException(e.getMessage());
        } catch(Exception e){
            LOGGER.info(e.getMessage());
        }
        if(userEntity != null && facebookUser != null){ // some more ifs prbly
            this.userAuthentication.setUserEntity(userEntity);
            this.userAuthentication.setFbUser(facebookUser);
            userAuthentication.setAuthenticated(true);
            this.userAuthentication.setCredentials(token);
        }
        else{
            throw new FacebookAuthenticationException("Creating user went wrong!");
        }
        return this.userAuthentication;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass == UserAuthentication.class;
    }

    @Autowired
    public void setFacebookService(FacebookService facebookService){
        this.facebookService = facebookService;
    }

    @Autowired
    public void setUserAuthentication(UserAuthentication userAuthentication) {
        this.userAuthentication = userAuthentication;
    }

    @Autowired
    public void setFacebookConverter(FacebookConverter facebookConverter){
        this.facebookConverter = facebookConverter;
    }
}
