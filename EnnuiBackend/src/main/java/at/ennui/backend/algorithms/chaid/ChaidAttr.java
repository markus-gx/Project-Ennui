package at.ennui.backend.algorithms.chaid;

import at.ennui.backend.algorithms.chaid.attributes.DayAttr;
import at.ennui.backend.algorithms.model.AlgorithmDTO;

import java.util.ArrayList;
import java.util.List;

public class ChaidAttr {
    private List<AttributeValue> values;
    private Class<? extends AttributeValue> attrClass;
    private static double ALPHA_LEVEL = 0.01;


    public ChaidAttr(List<String> attrValues, Class<? extends AttributeValue> attrClass, List<AlgorithmDTO> trainingsData ) throws Exception {
        this.values = new ArrayList<>();
        this.attrClass = attrClass;
        for(int i = 0; i < attrValues.size(); i++) {
            this.values.add(attrClass.getConstructor(String.class, List.class).newInstance(attrValues.get(i), trainingsData));
        }
    }
    public ChaidAttr(List<AttributeValue> vals, Class<? extends AttributeValue> attrClass) {
        this.values = vals;
        this.attrClass =  attrClass;
    }
    public List<AttributeValue> getValues() {
        return values;
    }

    public void setValues(List<AttributeValue> values) {
        this.values = values;
    }

    public Class<? extends AttributeValue> getAttrClass() {
        return attrClass;
    }

    public void setAttrClass(Class<? extends AttributeValue> attrClass) {
        this.attrClass = (Class<DayAttr>) (Class<DayAttr>) attrClass;
    }

    public List<AttributeValue> prune() {
        mergeLeastSignificantPreditors(values);
        return values;
    }
    public void mergeLeastSignificantPreditors(List<AttributeValue> predictors) {

        AttributeValue maxP1 = null;
        AttributeValue maxP2 = null;

        double maxPval = 0;
        for(int i1 = 0; i1 < predictors.size(); i1++){
            for(int i2 = i1; i2 < predictors.size(); i2++) {

                AttributeValue p1 = predictors.get(i1);
                AttributeValue p2 = predictors.get(i2);

                if(!p1.equals(p2)) {
                    double pValue = p1.compareChiSquare(p2);

                    if((maxP1 == null && maxP2 == null) || pValue > maxPval) {
                        maxP1 = p1;
                        maxP2 = p2;

                        maxPval = pValue;
                    }
                }
            }
        }

        if(maxPval >= ALPHA_LEVEL && predictors.size() > 2) {
            maxP1.merge(maxP2);
            predictors.remove(maxP2);
            mergeLeastSignificantPreditors(predictors);
        }
    }


    public int getOriginalValueCount() {
        int val = 0;
        for(AttributeValue p : this.values) {
            val += p.getGrade();
        }
        return val;
    }
    public ChaidAttr removeValue(AttributeValue p) {
        this.values.remove(p);
        return this;
    }
    public ChaidAttr addValue(AttributeValue p){
        this.values.add(p);
        return this;
    }
    public double getAdjustedPValue() {
        double val = 0;

        int r = this.values.size();
        int c = this.getOriginalValueCount();
        for(int i = 0; i < r; i++) {
            val += Math.pow(-1, i) * (Math.pow((r - i), c)) / (fact(r) * fact(r-i));
        }

        return val;
    }
    public ChaidAttr clone() {
        List<AttributeValue> list = new ArrayList<>();

        for(AttributeValue v : this.values) {
            list.add(v.clone());
        }

        return new ChaidAttr(list, this.getAttrClass());
    }
    private int fact(int n) {

        int result;
        if(n==1) {
            return 1;
        }

        result = fact(n-1) * n;
        return result;
    }
}
