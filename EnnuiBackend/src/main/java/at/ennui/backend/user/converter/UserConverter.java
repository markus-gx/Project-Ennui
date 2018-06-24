package at.ennui.backend.user.converter;

import at.ennui.backend.user.model.UserDto;
import at.ennui.backend.user.model.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    private ModelMapper modelMapper;

    public UserDto convert(UserEntity entity){
        return modelMapper.map(entity,UserDto.class);
    }

    public UserEntity convert(UserDto entity){
        return modelMapper.map(entity,UserEntity.class);
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }
}
