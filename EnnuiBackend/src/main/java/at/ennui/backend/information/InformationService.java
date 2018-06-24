package at.ennui.backend.information;

import at.ennui.backend.information.controller.InformationController;
import at.ennui.backend.information.model.StatisticsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InformationService {
    private InformationController informationController;

    public Object getTaxis(double lat, double longitude){
        return informationController.getTaxis(lat,longitude);
    }

    public StatisticsDto getStatistics(){
        return informationController.getStatistics();
    }

    @Autowired
    public void setInformationController(InformationController informationController){
        this.informationController = informationController;
    }
}
