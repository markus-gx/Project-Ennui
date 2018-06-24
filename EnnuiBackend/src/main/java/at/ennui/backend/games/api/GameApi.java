package at.ennui.backend.games.api;

import at.ennui.backend.games.GameService;
import at.ennui.backend.games.model.GameDto;
import at.ennui.backend.games.model.GameEntity;
import at.ennui.backend.games.model.GameRatingEntity;
import at.ennui.backend.main.models.Holder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Martin Singer on 19.09.2017.
 */
@RestController
@RequestMapping(value = "/games")
@CrossOrigin
public class GameApi {
    private GameService gameService;

    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    public Holder<GameDto> editGame(@RequestBody GameDto e){
        return gameService.editGame(e);
    }

    @RequestMapping(value = "/activated",method = RequestMethod.GET)
    public Holder<GameDto> getAllGames(){
        return gameService.getActivatedGames();
    }

    @RequestMapping(value = "/activatedlogged",method = RequestMethod.GET)
    public  Holder<GameDto> getAllGamesLoggedIn(){
        return gameService.getActivatedGames();
    }

    @RequestMapping(value = "/notactivated",method = RequestMethod.GET)
    public  Holder<GameDto> getNonActivatedGames(){
        return gameService.getNotActivatedGames();
    }

    @RequestMapping(value = "/activate/{id}",method = RequestMethod.POST)
    public  Holder<GameDto> activateGame(@PathVariable("id") long id){
        return gameService.activateGame(id);
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public  Holder<GameDto> addGame(@RequestBody GameDto dto){
        return gameService.addGame(dto);
    }

    @RequestMapping(value = "/delete/{id}",method = RequestMethod.POST)
    public  Holder<GameDto> deleteGame(@PathVariable("id") long id){
        return gameService.deleteGame(id);
    }

    @RequestMapping(value = "/rate",method = RequestMethod.POST)
    public  Holder<GameDto> rateGame(@RequestBody GameRatingEntity entity){
        return gameService.rate(entity);
    }

    @RequestMapping(value = "/favorize/{id}",method = RequestMethod.POST)
    public  Holder<GameDto> favorizeGame(@PathVariable("id") long id){
        return gameService.favorize(id);
    }

    @RequestMapping(value = "/unfavorize/{id}",method = RequestMethod.POST)
    public  Holder<GameDto> unfavorizeGame(@PathVariable("id") long id){
        return gameService.unfavorize(id);
    }

    @RequestMapping(value = "/mysubmissions",method = RequestMethod.GET)
    public  Holder<GameDto> getUserSubmissions(){
        return gameService.getUserSubmissions();
    }

    @Autowired
    public void setGameService(GameService gameService){
        this.gameService = gameService;
    }

}
