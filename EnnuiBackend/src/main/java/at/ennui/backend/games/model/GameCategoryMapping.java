package at.ennui.backend.games.model;

import at.ennui.backend.games.configuration.GameCategories;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "game_cat_mapping")
public class GameCategoryMapping {
    @Id @GeneratedValue
    private long id;
    private long gameId;
    private GameCategories categoryId;

    public GameCategoryMapping(){

    }

    public GameCategoryMapping(long id, GameCategories g){
        this.gameId = id;
        this.categoryId = g;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public GameCategories getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(GameCategories categoryId) {
        this.categoryId = categoryId;
    }
}
