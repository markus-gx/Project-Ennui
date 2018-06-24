package at.ennui.backend.games.model;

import at.ennui.backend.games.configuration.GameCategories;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin Singer on 19.09.2017.
 */
public class GameDto {
    private Long id;
    private String name;
    private String description;
    private String instruction;
    private String cover;
    private int minPlayer;
    private int maxPlayer;
    private boolean activated;
    private long ownerId;
    private List<GameCategories> categories;
    private int rating;
    private boolean ratedByUser;

    public boolean isRatedByUser() {
        return ratedByUser;
    }

    public void setRatedByUser(boolean ratedByUser) {
        this.ratedByUser = ratedByUser;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getMinPlayer() {
        return minPlayer;
    }

    public void setMinPlayer(int minPlayer) {
        this.minPlayer = minPlayer;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public void addCategory(GameCategories categories){
        if(getCategories() == null){
            setCategories(new ArrayList<>());
        }
        this.categories.add(categories);
    }

    public List<GameCategories> getCategories() {
        return categories;
    }

    public void setCategories(List<GameCategories> categories) {
        this.categories = categories;
    }
}
