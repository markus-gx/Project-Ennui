package at.ennui.backend.algorithms.chaid;


import at.ennui.backend.algorithms.model.AlgorithmDTO;

import java.util.List;

public abstract class AttributeValue {
    private String name;
    private int recommendedCount;
    private int notRecommendedCount;
    private int grade;
    private List<AlgorithmDTO> events;

    public AttributeValue(String name, List<AlgorithmDTO> events, int grade) {
        this.name = name;
        this.events = events;
        this.grade = grade;
        this.setData(events);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getRecommendedCount() {
        return recommendedCount;
    }

    public void setRecommendedCount(int recommendedCount) {
        this.recommendedCount = recommendedCount;
    }

    public int getNotRecommendedCount() {
        return notRecommendedCount;
    }

    public void setNotRecommendedCount(int notRecommendedCount) {
        this.notRecommendedCount = notRecommendedCount;
    }

    public int getSum() {
        return this.recommendedCount + this.notRecommendedCount;
    }

    public int getGrade() {
        return grade;
    }

    public List<AlgorithmDTO> getEvents() {
        return this.events;
    }
    public void setEvents(List<AlgorithmDTO> data) {
        this.events = data;
    }
    public void setGrade(int grade) {
        this.grade = grade;
    }
    public int[] getDataPerRecommendation() {
        int[] array = {this.notRecommendedCount, this.recommendedCount};
        return array;
    }

    public abstract AttributeValue clone();
    public abstract void setData(List<AlgorithmDTO> events);
    public abstract boolean test(AlgorithmDTO e);

    public double compareChiSquare(AttributeValue p2) {
        double chiSquareScore = 0;
        int degreesOfFreedom = 1;
        int[] clickRateSums = new int[2];
        for(int i = 0; i < clickRateSums.length; i++) {
            clickRateSums[i] = this.getDataPerRecommendation()[i] + p2.getDataPerRecommendation()[i];
            if(clickRateSums[i] == 0) {
                degreesOfFreedom--;
                return 1;
            }
        }
        for(int i = 0; i < this.getDataPerRecommendation().length; i++) {
            double expected = (clickRateSums[i]*this.getSum())/(this.getSum() + p2.getSum());
            double actual = this.getDataPerRecommendation()[i];

            if(clickRateSums[i] != 0) {
                chiSquareScore+= chiSquareCheck(actual, expected);
            }
        }
        for(int i = 0; i < p2.getDataPerRecommendation().length; i++) {
            double expected = clickRateSums[i]*p2.getSum()/(this.getSum() + p2.getSum());
            double actual = p2.getDataPerRecommendation()[i];

            if(clickRateSums[i] != 0)  {
                chiSquareScore+= chiSquareCheck(actual, expected);
            }
        }
        return PValueCalculator.pochisq(chiSquareScore, degreesOfFreedom);
    }
    private double chiSquareCheck(double actual, double expected) {
        return ((actual - expected) *  (actual-expected)) / expected;
    }
    public boolean merge(AttributeValue p2) {
        if(this.getClass() == p2.getClass()) {
            this.name += "," + p2.name;
            this.recommendedCount += p2.recommendedCount;
            this.notRecommendedCount += p2.notRecommendedCount;
            this.grade += p2.getGrade();
            return true;
        }
        return false;
    }

}
