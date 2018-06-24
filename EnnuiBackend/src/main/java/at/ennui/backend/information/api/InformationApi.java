package at.ennui.backend.information.api;

import at.ennui.backend.information.InformationService;
import at.ennui.backend.information.model.StatisticsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/information")
public class InformationApi {
    private InformationService informationService;

    @RequestMapping(value = "/statistics",method = RequestMethod.GET)
    public StatisticsDto getStatistics(){
        return informationService.getStatistics();
    }

    @RequestMapping(value = "/taxis",method = RequestMethod.POST)
    public Object getTaxis(@RequestBody LocationBody locationBody){
        return informationService.getTaxis(locationBody.getLatitude(),locationBody.getLongitude());
    }

    @Autowired
    public void setInformationService(InformationService informationService){
        this.informationService = informationService;
    }

    private static class LocationBody{
        private Double longitude;
        private Double latitude;

        public Double getLatitude() {
            return latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }
    }
}
