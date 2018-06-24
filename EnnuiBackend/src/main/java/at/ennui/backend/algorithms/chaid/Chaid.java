package at.ennui.backend.algorithms.chaid;


import at.ennui.backend.algorithms.model.AlgorithmDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Chaid {
    private static double ALPHA_LEVEL = 0.01;
    private List<AlgorithmDTO> trainingsData;
    private List<ChaidAttr> originalChaidAttrs;
    private List<ChaidAttr> prunedChaidAttrs;
    private List<Chaid> children;
    private ChaidAttr splitBy;
    private int level;
    private boolean isTerminal;
    private AttributeValue label;

    public Chaid(List<AlgorithmDTO> td, List<ChaidAttr> chaidAttrs) throws Exception {
        this(td, chaidAttrs, 0);
    }
    private Chaid(List<AlgorithmDTO> td, List<ChaidAttr> chaidAttrs, int level) throws Exception {
        this.trainingsData = td;
        this.originalChaidAttrs = new ArrayList<>();
        for (ChaidAttr p : chaidAttrs) {
            this.originalChaidAttrs.add(p.clone());
        }
        for(ChaidAttr p : this.originalChaidAttrs) {
            for(AttributeValue v : p.getValues()) {
                v.setData(td);
            }
        }
        this.prunedChaidAttrs = removeUnnecessaryAttributeValues(this.originalChaidAttrs);
        this.children = new ArrayList<>();
        this.level = level;
        this.isTerminal = false;
    }

    public void split() throws Exception{
        List<ChaidAttr> unnecessaryArrts = new ArrayList<>();
        for(ChaidAttr p : this.originalChaidAttrs) {
            if(!this.prunedChaidAttrs.stream().map(ChaidAttr::getAttrClass).collect(Collectors.toList()).contains(p.getAttrClass())) {
                unnecessaryArrts.add(p);
            }
        }
        this.originalChaidAttrs.removeAll(unnecessaryArrts);
        double minPVal = -1;
        for(ChaidAttr p : this.prunedChaidAttrs) {
            p.prune();
            double adjPVal = p.getAdjustedPValue();
            if(minPVal == -1 || adjPVal < minPVal) {
                minPVal = adjPVal;
                this.splitBy = p;
            }
        }


        List<ChaidAttr> newChaidAttrs = new ArrayList<>();
        for(ChaidAttr p : this.originalChaidAttrs) {
            if(p.getAttrClass() != this.splitBy.getAttrClass()) {
                newChaidAttrs.add(p);
            }
        }
        for(AttributeValue v : this.splitBy.getValues()) {
            List<AlgorithmDTO> newTrainingsData = new ArrayList<>();
            for(AlgorithmDTO e : this.trainingsData) {
                if(v.test(e)) {
                    newTrainingsData.add(e);
                }
            }
            Chaid child = new Chaid(newTrainingsData, newChaidAttrs, this.level+1);
            child.setLabel(v);
            if(child.prunedChaidAttrs.size() >= 2) {
                child.split();
            }
            else {
                child.isTerminal = true;
            }
            children.add(child);
        }
    }
    public void checkEvent(AlgorithmDTO e) {
        if(!this.isTerminal) {
            for(Chaid child : this.children) {
                if(child.getLabel().test(e)) {
                    child.checkEvent(e);
                }
            }
        }
        else {
            e.setConfidenceLevel(this.getConfidenceLevel());
        }
    }
    public double getConfidenceLevel() {
        double recommended = this.label.getRecommendedCount();
        double sum = this.label.getSum();
        double confidenveLevel = (recommended/sum) * 100;
        return confidenveLevel;
    }

    public AttributeValue getLabel() {
        return label;
    }
    public void setLabel(AttributeValue label) {
        this.label = label;
    }

    private List<ChaidAttr> removeUnnecessaryAttributeValues(List<ChaidAttr> base) throws Exception{
        List<ChaidAttr> pruned = new ArrayList<>();

        for(ChaidAttr a : base) {
            ChaidAttr newAttr = new ChaidAttr(new ArrayList<AttributeValue>(), a.getAttrClass());
            for(AttributeValue v : a.getValues()) {
                if (v.getSum() > 0) {
                    newAttr.addValue(v.clone());
                }
            }
            if(newAttr.getValues().size() > 1) {
                pruned.add(newAttr);
            }
        }
        return pruned;
    }

}
