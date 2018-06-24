package at.ennui.backend.user.model;

import at.ennui.backend.events.model.EventDto;
import at.ennui.backend.games.model.GameDto;
import org.springframework.social.facebook.api.AgeRange;

import java.util.List;

public class UserDto {
    private Long id;
    private String fbId;
    private String firstname;
    private String lastname;
    private String name;
    private AgeRange ageRange;
    private String email;
    private String gender;
    private boolean admin;
    private String profileImage;
    private List<EventDto> favouriteEvents;
    private List<GameDto> favouriteGames;

    public List<GameDto> getFavouriteGames() {
        return favouriteGames;
    }

    public void setFavouriteGames(List<GameDto> favouriteGames) {
        this.favouriteGames = favouriteGames;
    }

    public List<EventDto> getFavouriteEvents() {
        return favouriteEvents;
    }

    public void setFavouriteEvents(List<EventDto> favouriteEvents) {
        this.favouriteEvents = favouriteEvents;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AgeRange getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(AgeRange ageRange) {
        this.ageRange = ageRange;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
