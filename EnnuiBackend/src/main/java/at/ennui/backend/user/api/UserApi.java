package at.ennui.backend.user.api;

import at.ennui.backend.user.UserService;
import at.ennui.backend.user.model.UserDto;
import at.ennui.backend.user.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/users")
@CrossOrigin
public class UserApi {
    private UserService userService;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public UserDto getUserData(){
        return userService.loginUser();
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
