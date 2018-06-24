package at.ennui.backend.facebook.converter;

import at.ennui.backend.events.model.EventDto;
import at.ennui.backend.user.model.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.Invitation;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FacebookConverter {
    private ModelMapper modelMapper;

    public UserEntity convertFacebookUserToUserDto(User user){
        UserEntity u = modelMapper.map(user,UserEntity.class);
        u.setFbId(user.getId());
        u.setId(null);
        return u;
    }

    public List<EventDto> convertInvitationsToEventDtos(PagedList<Invitation> invitations){
        return invitations.stream().map(this::convertInvitationToEventDto).collect(Collectors.toList());
    }

    public EventDto convertInvitationToEventDto(Invitation invitation){
        EventDto e = new EventDto();

        return e;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }
}
