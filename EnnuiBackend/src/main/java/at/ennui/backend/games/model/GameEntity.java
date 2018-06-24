package at.ennui.backend.games.model;
import javax.persistence.*;

/**
 * Created by Martin Singer on 19.09.2017.
 */
@Entity
@Table(name = "games")
public class GameEntity {
    @Id @GeneratedValue
    private Long id;
    private String name;
    @Column(columnDefinition = "text(65535)")
    private String description;
    @Column(columnDefinition = "text(65535)")
    private String instruction;
    @Column(columnDefinition = "text(1000)")
    private String cover;
    private int minPlayer;
    private int maxPlayer;
    private boolean activated;
    private long ownerId;

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
}