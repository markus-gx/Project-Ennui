package at.ennui.backend.games.controller;

import at.ennui.backend.games.configuration.GameCategories;
import at.ennui.backend.games.converter.GameConverter;
import at.ennui.backend.games.model.*;
import at.ennui.backend.games.repository.FavorizedGamesRepository;
import at.ennui.backend.games.repository.GameCategoriesRepository;
import at.ennui.backend.games.repository.GameRatingRepository;
import at.ennui.backend.games.repository.GameRepository;
import at.ennui.backend.main.models.Holder;
import at.ennui.backend.user.UserService;
import at.ennui.backend.user.model.UserEntity;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Martin Singer on 19.09.2017.
 */
@Component
public class GameController {
    private GameRepository gameRepository;
    private GameCategoriesRepository gameCategoriesRepository;
    private FavorizedGamesRepository favorizedGamesRepository;
    private GameRatingRepository gameRatingRepository;
    private GameConverter gameConverter;
    private UserService userService;

    public Holder<GameDto> editGame(GameDto entity){
        UserEntity userEntity = userService.getCurrentUser();
        GameEntity old = gameRepository.findOne(entity.getId());
        if(userEntity.isAdmin() || userEntity.getId().equals(old.getOwnerId())){
            entity.setOwnerId(userEntity.getId());
            entity.setId(old.getId());
            gameCategoriesRepository.deleteByGameId(entity.getId());
            saveCategories(entity.getCategories(), entity.getId());
            gameRepository.save(gameConverter.convert(entity));
            return new Holder<>(true,"Updated!");
        }
        return new Holder<>(false,"You do not have permission to perform this command!");
    }

    public Holder<GameDto> getUserSubmissions(){
        UserEntity u = userService.getCurrentUser();
        if(u != null){
            Holder<GameDto> g = new  Holder<GameDto>(true,gameConverter.convert(gameRepository.findByOwnerId(u.getId())));
            g.setMessage("submissions");
            return g;
        }
        return new  Holder<GameDto>(false,"User not found!");
    }

    public List<GameDto> getFavorizedGamesByUser(long usrId){
        List<Long> gameids = favorizedGamesRepository.findGameIdByUserId(usrId);
        return gameConverter.convert(gameRepository.findAll(gameids));
    }

    public  Holder<GameDto> rate(GameRatingEntity entity){
        UserEntity u = userService.getCurrentUser();
        if(u != null && gameRepository.findOne(entity.getGameId()) != null){
            GameRatingEntity rating = gameRatingRepository.findByGameIdAndUserId(entity.getGameId(),userService.getCurrentUser().getId());
            if(rating != null){
                rating.setRating(entity.getRating());
                gameRatingRepository.save(rating);
            }
            else{
                rating = new GameRatingEntity();
                rating.setRating(entity.getRating());
                rating.setGameId(entity.getGameId());
                rating.setUserId(userService.getCurrentUser().getId());
                gameRatingRepository.save(rating);
            }
            return new  Holder<GameDto>(true,gameConverter.convert(gameRepository.findOne(entity.getGameId())));
        }
        else{
            return new  Holder<GameDto>(false,"User or Game not found!");
        }
    }

    public  Holder<GameDto> unfavorize(long gameId){
        UserEntity u = userService.getCurrentUser();
        if(u != null){
            FavorizedGameEntity e = favorizedGamesRepository.findByUserIdAndGameId(u.getId(),gameId);
            if(e != null){
                favorizedGamesRepository.delete(e.getId());
                return new  Holder<GameDto>(true);
            }
            else return new  Holder<GameDto>(false,"Mapping not found!");
        }
        return new  Holder<GameDto>(false);
    }

    public  Holder<GameDto> favorize(long id){
        UserEntity u = userService.getCurrentUser();
        if(u != null){
            FavorizedGameEntity entity = new FavorizedGameEntity();
            if(favorizedGamesRepository.findByUserIdAndGameId(u.getId(),id) == null){
                if(gameRepository.findOne(id) != null){
                    entity.setGameId(id);
                    entity.setUserId(u.getId());
                    favorizedGamesRepository.save(entity);
                    return new  Holder<GameDto>(true);
                }
                else return new  Holder<GameDto>(false, "Game not found!");
            }
            else return new  Holder<GameDto>(true,"Already favored!");
        }
        return new  Holder<GameDto>(false);
    }

    public  Holder<GameDto> deleteGame(long id){
        try{
            gameRepository.delete(id);
            gameCategoriesRepository.deleteByGameId(id);
            favorizedGamesRepository.deleteByGameId(id);
            return new  Holder<GameDto>(true);
        }
        catch(Exception e){
            return new  Holder<GameDto>(false);
        }
    }

    public  Holder<GameDto> addGame(GameDto dto){
        Holder<GameDto> holder = new  Holder<GameDto>(false);
        if(dto != null){
            if(dto.getCategories() != null && !dto.getCategories().isEmpty() && dto.getName() != null && dto.getDescription() != null && dto.getInstruction() != null
                    && dto.getMaxPlayer() > dto.getMinPlayer()){
                UserEntity userDto = userService.getCurrentUser();
                dto.setOwnerId(userDto.getId());
                dto.setActivated(false);
                dto.setId(null);
                GameEntity e = gameRepository.save(gameConverter.convert(dto));
                saveCategories(dto.getCategories(),e.getId());
                holder.setSuccess(true);
                holder.setMessage("Game Added!");
            }
            else{
                holder.setMessage("Some fields are missing!");
            }
        }
        return holder;
    }

    private void saveCategories(List<GameCategories> list, long gameId){
        List<GameCategoryMapping> mappings = list.stream().map(g -> new GameCategoryMapping(gameId,g)).collect(Collectors.toList());
        gameCategoriesRepository.save(mappings);
    }

    public  Holder<GameDto> activateGame(long id){
        GameEntity entity = gameRepository.findOne(id);
        if(entity != null){
            entity.setActivated(true);
            gameRepository.save(entity);
            return new  Holder<GameDto>(true,"activated");
        }
        return new  Holder<GameDto>(false,"Game not found!");
    }

    public  Holder<GameDto> getActivatedGames(){
        List<GameEntity> gameEntities = gameRepository.findByActivated(true);
        return new  Holder<GameDto>(true,gameConverter.convert(gameEntities));
    }

    public  Holder<GameDto> getNotActivatedGames(){
        if(userService.getCurrentUser().isAdmin()) {
            List<GameEntity> gameEntities = gameRepository.findByActivated(false);
            Holder h = new  Holder<GameDto>(true,gameConverter.convert(gameEntities));
            h.setMessage("games");
            return h;
        }
        return new Holder<GameDto>(false,"You do not have permission to perform this command!");
    }

    @Autowired
    public void setGameRatingRepository(GameRatingRepository gameRatingRepository){
        this.gameRatingRepository = gameRatingRepository;
    }

    @Autowired
    public void setFavorizedGamesRepository(FavorizedGamesRepository favorizedGamesRepository) {
        this.favorizedGamesRepository = favorizedGamesRepository;
    }

    @Autowired
    public void setGameCategoriesRepository(GameCategoriesRepository gameCategoriesRepository){
        this.gameCategoriesRepository = gameCategoriesRepository;
    }

    @Autowired
    public void setGameRepository(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    @Autowired
    public void setGameConverter(GameConverter gameConverter){
        this.gameConverter = gameConverter;
    }
}
