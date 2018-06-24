package at.ennui.backend.games.converter;

import at.ennui.backend.games.configuration.GameCategories;
import at.ennui.backend.games.model.GameCategoryMapping;
import at.ennui.backend.games.model.GameDto;
import at.ennui.backend.games.model.GameEntity;
import at.ennui.backend.games.model.GameRatingEntity;
import at.ennui.backend.games.repository.GameCategoriesRepository;
import at.ennui.backend.games.repository.GameRatingRepository;
import at.ennui.backend.user.UserService;
import javassist.NotFoundException;
import org.bouncycastle.math.raw.Mod;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Martin Singer on 19.09.2017.
 */
@Component
public class GameConverter {
    private ModelMapper modelMapper;
    private GameCategoriesRepository gameCategoriesRepository;
    private GameRatingRepository gameRatingRepository;
    private UserService userService;

    public List<GameDto> convert(List<GameEntity> list){
        return list.stream().map(this::convert).collect(Collectors.toList());
    }

    public GameDto convert(GameEntity entity){
        GameDto gameDto = modelMapper.map(entity,GameDto.class);
        List<GameCategoryMapping> gameCategories = gameCategoriesRepository.findByGameId(gameDto.getId());
        gameCategories.forEach(c -> gameDto.addCategory(c.getCategoryId()));
        List<GameRatingEntity> gameRatingEntities = gameRatingRepository.findByGameId(gameDto.getId());
        gameDto.setRatedByUser(false);

        if(gameRatingEntities != null && gameRatingEntities.size() > 0){
            gameDto.setRating(gameRatingEntities.stream().mapToInt(GameRatingEntity::getRating).sum() / gameRatingEntities.size());
            if(userService.getCurrentUser() != null){
                GameRatingEntity e = gameRatingRepository.findByGameIdAndUserId(gameDto.getId(),userService.getCurrentUser().getId());
                if(e != null){
                    gameDto.setRatedByUser(true);
                    gameDto.setRating(e.getRating());
                }
            }
        }
        return gameDto;
    }

    public GameEntity convert(GameDto dto){
        return modelMapper.map(dto,GameEntity.class);
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setGameRatingRepository(GameRatingRepository gameRatingRepository) {
        this.gameRatingRepository = gameRatingRepository;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Autowired
    public void setGameCategoriesRepository(GameCategoriesRepository gameCategoriesRepository) {
        this.gameCategoriesRepository = gameCategoriesRepository;
    }
}
