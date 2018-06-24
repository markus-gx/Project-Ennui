package at.ennui.backend.games;

import at.ennui.backend.games.controller.GameController;
import at.ennui.backend.games.model.GameDto;
import at.ennui.backend.games.model.GameEntity;
import at.ennui.backend.games.model.GameRatingEntity;
import at.ennui.backend.main.models.Holder;
import at.ennui.backend.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Martin Singer on 19.09.2017.
 */
@Component
public class GameService {
    private GameController gameController;
    private UserService userService;

    public Holder<GameDto> editGame(GameDto entity){
        return gameController.editGame(entity);
    }

    public Holder<GameDto> getUserSubmissions(){
        return gameController.getUserSubmissions();
    }

    public List<GameDto> getFavorizedGamesByUser(long usrId){
       return gameController.getFavorizedGamesByUser(usrId);
    }

    public  Holder<GameDto> rate(GameRatingEntity ratingEntity){
        if(ratingEntity.getRating() > 0 && ratingEntity.getRating() <= 5){
            return gameController.rate(ratingEntity);
        }
        return new  Holder<GameDto>(false,"Body is invalid!");
    }

    public  Holder<GameDto> unfavorize(long id){
        return gameController.unfavorize(id);
    }

    public  Holder<GameDto> favorize(long id){
        return gameController.favorize(id);
    }

    public  Holder<GameDto> deleteGame(long id){
        if(userService.getCurrentUser() != null) {
            if (userService.getCurrentUser().isAdmin()) {
                return gameController.deleteGame(id);
            } else {
                return new  Holder<GameDto>(false, "You do not have permission to access this endpoint!");
            }
        }
        else {
            return new  Holder<GameDto>(false,"User not found!");
        }
    }

    public  Holder<GameDto> addGame(GameDto dto){
        return gameController.addGame(dto);
    }

    public  Holder<GameDto> getActivatedGames(){
        return gameController.getActivatedGames();
    }

    public  Holder<GameDto> getNotActivatedGames(){
        if(userService.getCurrentUser() != null) {
            if (userService.getCurrentUser().isAdmin()) {
                return gameController.getNotActivatedGames();
            } else {
                return new  Holder<GameDto>(false, "You do not have permission to access this endpoint!");
            }
        }
        else {
            return new  Holder<GameDto>(false,"User not found!");
        }

    }

    public  Holder<GameDto> activateGame(long id){
        if(userService.getCurrentUser() != null){
            if(userService.getCurrentUser().isAdmin()){
                return gameController.activateGame(id);
            }
            else{
                return new  Holder<GameDto>(false,"You do not have permission to access this resource!");
            }
        }
        else{
            return new  Holder<GameDto>(false,"User not found!");
        }
    }

    @Autowired
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
