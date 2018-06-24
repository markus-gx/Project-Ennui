package at.ennui.backend.algorithms.model;

import java.util.Date;

/**
 * Created by Martin Singer on 03.01.2018.
 */
public class AlgorithmDTO {
    private String weekday;
    private int weekday_id;
    private int category_id;
    private int additionalTag_id;
    private int week_id;
    private long id;
    private String name;
    private Date start_time_converted;
    private long start_hour;
    private String category;
    private String additional_tag;
    private String city;
    private String week;
    private String operator;
    private String recommended;
    private double confidenceLevel;

    public double getConfidenceLvl() {
        return confidenceLvl;
    }

    public void setConfidenceLvl(double confidenceLvL) {
        this.confidenceLvl = confidenceLvL;
    }

    private double confidenceLvl;


    public int getWeekday_id() {
        return weekday_id;
    }

    public void setWeekday_id(int weekday_id) {
        this.weekday_id = weekday_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getAdditionalTag_id() {
        return additionalTag_id;
    }

    public void setAdditionalTag_id(int additionalTag_id) {
        this.additionalTag_id = additionalTag_id;
    }

    public int getWeek_id() {
        return week_id;
    }

    public void setWeek_id(int week_id) {
        this.week_id = week_id;
    }

    public String getRecommended() {
        return recommended;
    }

    public void setRecommended(String recommended) {
        this.recommended = recommended;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public long getStart_hour() {
        return start_hour;
    }

    public void setStart_hour(long start_hour) {
        this.start_hour = start_hour;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAdditional_tag() {
        return additional_tag;
    }

    public void setAdditional_tag(String additional_tag) {
        this.additional_tag = additional_tag;
    }
    public Date getStart_time_converted() {
        return start_time_converted;
    }

    public void setStart_time_converted(Date start_time_converted) {
        this.start_time_converted = start_time_converted;
    }

    public double getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(double confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }
}
