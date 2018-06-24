package at.ennui.backend.main.models;

import at.ennui.backend.events.model.EventDto;

import java.util.ArrayList;
import java.util.List;

public class Holder<T> {
    private boolean success;
    private String message;
    private List<T> result;
    private List<T> recommendedResults;

    public Holder(){

    }

    public Holder(boolean success, List<T> list){
        this.success = success;
        this.result = list;
    }

    public Holder(boolean success, T obj){
        result = new ArrayList<>();
        result.add(obj);
        this.success = success;
    }

    public Holder(boolean success, String msg){
        this.success = success;
        this.message = msg;
    }

    public Holder(boolean success){
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public List<T> getRecommendedResults() {
        return recommendedResults;
    }

    public void setRecommendedResults(List<T> recommendedResults) {
        this.recommendedResults = recommendedResults;
    }
}
