package at.ennui.backend.algorithms.chaid.attributes;


import at.ennui.backend.algorithms.chaid.AttributeValue;
import at.ennui.backend.algorithms.model.AlgorithmDTO;

import java.util.List;

public class LocationAttr extends AttributeValue {

    public LocationAttr(String name, List<AlgorithmDTO> events) {
        super(name, events, 1);
    }
    public LocationAttr(String name, List<AlgorithmDTO> events, int grade) {
        super(name, events, grade);
    }

    @Override
    public AttributeValue clone() {
        return new LocationAttr(this.getName(), this.getEvents(),this.getGrade());
    }

    @Override
    public void setData(List<AlgorithmDTO> events) {
        this.setRecommendedCount(0);
        this.setNotRecommendedCount(0);
        for (AlgorithmDTO e : events) {
            if(e.getCity().equals(this.getName())) {
                if(e.getRecommended().equals("yes")) {
                    this.setRecommendedCount(this.getRecommendedCount() + 1);
                }
                else {
                    this.setNotRecommendedCount(this.getNotRecommendedCount() + 1);
                }
            }
        }
    }

    public String toString() {
        return this.getName() + " | " + this.getNotRecommendedCount() + " | " + this.getRecommendedCount();
    }

    @Override
    public boolean test(AlgorithmDTO e) {
        if(this.getName().contains(e.getCity())) {
            return true;
        }
        return false;
    }
}
