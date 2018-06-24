package at.ennui.backend.user;

import at.ennui.backend.user.controller.UserController;
import at.ennui.backend.user.model.UserDto;
import at.ennui.backend.user.model.UserEntity;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.User;
import org.springframework.stereotype.Component;

@Component
public class UserService {
    private UserController userController;

    public long countAllUsers(){
        return userController.countAllUsers();
    }

    public UserEntity getCurrentUser() {
        return userController.getCurrentUser();
    }

    public User getUserDetails(){
        return userController.getFacebookProfile();
    }

    public String getUserToken(){
        return userController.getUserToken();
    }

    public UserDto loginUser(){
        return userController.loginUser();
    }

    @Autowired
    public void setUserController(UserController userController) {
        this.userController = userController;
    }
}
