package at.ennui.backend.algorithms;

import at.ennui.backend.algorithms.controller.AlgorithmController;
import at.ennui.backend.algorithms.model.AlgorithmDTO;
import at.ennui.backend.events.model.EventEntity;
import weka.core.Instances;

import java.util.List;

/**
 * Created by Martin Singer on 03.01.2018.
 */
public class AlgorithmService {
    private AlgorithmController algorithmController = new AlgorithmController();

    public List<Long> applyC45(List<AlgorithmDTO> events, Instances testset) throws Exception {
        if (events!= null){
            return algorithmController.applyC45(events, testset);
        }
        return null;
    }
    public List<Long> applyCART(List<AlgorithmDTO> events, Instances testset) throws Exception {
        if (events!= null){
            return algorithmController.applyCART(events, testset);
        }
        return null;
    }
    public List<Long> applyCHAID(List<AlgorithmDTO> events, List<AlgorithmDTO> testset) throws Exception {
        if (events!= null){
            return algorithmController.applyCHAID(events, testset);
        }
        return null;
    }
    public List<AlgorithmDTO> convertEventData(List<EventEntity> events) {
        if (events != null){
            return algorithmController.convertEventData(events);
        }
        return null;
    }
    public Instances prepareData(List<AlgorithmDTO> events){
        if (events!=null){
            return algorithmController.prepareData(events);
        }
        return null;
    }
    public Instances prepareTrainingSet(List<AlgorithmDTO> events){
        if (events!=null){
            return algorithmController.prepareTrainingSet(events);
        }
        return null;
    }
}
