package at.ennui.backend.security;

import at.ennui.backend.user.model.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.social.facebook.api.User;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class UserAuthentication implements Authentication {
    private UserEntity userEntity;
    private User fbUser;
    private boolean isAuthenticated;
    private String credentials;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getDetails() {
        return fbUser;
    }

    @Override
    public Object getPrincipal() {
        return userEntity;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {
        this.isAuthenticated = b;
    }

    @Override
    public String getName() {
        return userEntity.toString();
    }

    public void setUserEntity(UserEntity userEntity){
        this.userEntity = userEntity;
    }

    public void setCredentials(String token){
        this.credentials = token;
    }

    public void setFbUser(User u){
        this.fbUser = u;
    }
}
