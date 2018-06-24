package at.ennui.backend.algorithms.chaid.attributes;

import at.ennui.backend.algorithms.chaid.AttributeValue;
import at.ennui.backend.algorithms.model.AlgorithmDTO;

import java.util.List;

public class CategoryAttr extends AttributeValue {

    public CategoryAttr(String name, List<AlgorithmDTO> events, int grade) {
        super(name, events, grade);
    }
    public CategoryAttr(String name, List<AlgorithmDTO> events) {
        super(name, events, 1);
    }


    @Override
    public CategoryAttr clone() {
        return new CategoryAttr(this.getName(), this.getEvents(), this.getGrade());
    }

    @Override
    public void setData(List<AlgorithmDTO> events) {
        this.setEvents(events);
        this.setRecommendedCount(0);
        this.setNotRecommendedCount(0);
        for (AlgorithmDTO e : events) {
            if(e.getCategory().equals(this.getName())) {
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
        if(this.getName().contains(e.getCategory())) {
            return true;
        }
        return false;
    }
}