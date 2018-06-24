package at.ennui.backend.algorithms.controller;

import at.ennui.backend.algorithms.chaid.Chaid;
import at.ennui.backend.algorithms.chaid.ChaidAttr;
import at.ennui.backend.algorithms.chaid.attributes.CategoryAttr;
import at.ennui.backend.algorithms.chaid.attributes.DayAttr;
import at.ennui.backend.algorithms.chaid.attributes.TagAttr;
import at.ennui.backend.algorithms.chaid.attributes.WeekAttr;
import at.ennui.backend.algorithms.model.AlgorithmDTO;
import at.ennui.backend.events.model.EventEntity;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.SimpleCart;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.*;

/**
 * Created by Martin Singer on 03.01.2018.
 */
public class AlgorithmController {
    public List<Long> applyC45(List<AlgorithmDTO> events, Instances data) throws Exception {
        int cnt = 0;
        int confidentCnt = 0;
        data.setClassIndex(data.numAttributes() - 1);
        String[] options = {"-U"};
        J48 tree = new J48();         // new instance of tree
        tree.setOptions(options);     // set the options
        tree.setMinNumObj(1);
        tree.setUseLaplace(true);
        tree.setUseMDLcorrection(false);
        tree.buildClassifier(data);   // build classifier
        Instances dataRaw= prepareData(events);
        double highestConfidence = 0;
        for (int i = 0; i < dataRaw.numInstances(); i++) {
            double clsLabel = tree.classifyInstance(dataRaw.instance(i));
            double[] confidenceLevel = tree.distributionForInstance(dataRaw.instance(i));
            if(clsLabel ==0){
                events.get(i).setRecommended("no");
                events.get(i).setConfidenceLvl(confidenceLevel[0]);
            }
            if(clsLabel ==1){
                events.get(i).setRecommended("yes");
                events.get(i).setConfidenceLvl(confidenceLevel[1]);
                if (confidenceLevel[1]> highestConfidence){
                    highestConfidence = confidenceLevel[1];
                    confidentCnt ++;

                }
            }
        }
        ArrayList<Long> ids = new ArrayList<Long>();

        for(AlgorithmDTO event : events) {
            if (event.getRecommended() == "yes") {
                System.out.println(event.getName() + ": " + event.getConfidenceLvl());
                if (event.getConfidenceLvl() == highestConfidence){
                    ids.add(event.getId());
                    cnt++;
                }

            }
        }

        return ids;
    }
    public List<Long> applyCHAID(List<AlgorithmDTO> events, List<AlgorithmDTO> testSet) throws Exception{
        int confidenceCnt = 0;
        List<String> days = new ArrayList<>();
        List<String> locations = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        List<String> week = new ArrayList<>();
        List<String> additionalTags = new ArrayList<>();
        ArrayList<String> tag= new ArrayList<>();
        tag.addAll(Arrays.asList("club","festival","concert","reading","cabaret","art_exhibit","streetfood","food","shopping","sport","late_night","football","running","musical","theatre","other","health","gala_dinner","classic","rock"));
        ArrayList<String> coveredTags= new ArrayList<>();
        List<AlgorithmDTO> trainingsData = testSet;
        for (AlgorithmDTO event : trainingsData){
            event.setRecommended("yes");
            if (days.indexOf(event.getWeekday()) == -1){
                days.add(event.getWeekday());
            }
            if (locations.indexOf(event.getCity()) == -1){
                locations.add(event.getCity());
            }
            if (categories.indexOf(event.getCategory()) == -1){
                categories.add(event.getCategory());
            }
            if (week.indexOf(event.getWeek()) == -1){
                week.add(event.getWeek());
            }
            if (additionalTags.indexOf(event.getAdditional_tag()) == -1){
                additionalTags.add(event.getWeekday());
            }
            if (tag.indexOf(event.getAdditional_tag().toLowerCase()) != -1){
                if (!coveredTags.contains(event.getAdditional_tag().toLowerCase())){
                    coveredTags.add(event.getAdditional_tag().toLowerCase());
                }
            }

        }
        for (int i = 0; i< coveredTags.size(); i ++){
            String actualTag = coveredTags.get(i);
            ArrayList<AlgorithmDTO> taggedEvents = new ArrayList<>();
            events.stream().filter(o -> o.getAdditional_tag().equals(actualTag)).forEach(
                    o -> {
                        taggedEvents.add(o);
                    });
            AlgorithmDTO reverse = taggedEvents.get(0);
            reverse.setRecommended("yes");
            if (taggedEvents.stream().filter(o -> o.getWeek().equals("weekend")).findFirst().isPresent()){
                if (taggedEvents.stream().filter(o -> o.getWeek().equals("workday")).findFirst().isPresent() == false){
                    reverse.setWeekday("Mon");
                    reverse.setWeek("workday");
                    reverse.setRecommended("no");
                }
            }
            else if (taggedEvents.stream().filter(o -> o.getWeek().equals("workday")).findFirst().isPresent()){
                if (taggedEvents.stream().filter(o -> o.getWeek().equals("weekend")).findFirst().isPresent() == false){
                    reverse.setWeekday("Sat");
                    reverse.setWeek("weekend");
                    reverse.setRecommended("no");
                }
            }
            if (reverse.getRecommended().equals("no") ){
                testSet.add(reverse);
            }
            reverse = taggedEvents.get(0);
            if (taggedEvents.stream().filter(o -> o.getStart_hour() >= 15).findFirst().isPresent()){
                if (taggedEvents.stream().filter(o -> o.getStart_hour() < 15).findFirst().isPresent() == false){
                    reverse.setStart_hour(14);
                    reverse.setRecommended("no");
                }
            }
            else if (taggedEvents.stream().filter(o -> o.getStart_hour() < 15).findFirst().isPresent()){
                if (taggedEvents.stream().filter(o -> o.getStart_hour() >= 15).findFirst().isPresent() == false){
                    reverse.setStart_hour(15);
                    reverse.setRecommended("no");
                }
            }
            if (reverse.getRecommended().equals("no") ){
               testSet.add(reverse);
            }
            taggedEvents.clear();
        }
        tag.removeAll(coveredTags);
        for (int i = 0; i < tag.size(); i ++){
           AlgorithmDTO add = new AlgorithmDTO();
            add.setWeekday(days.get(i%days.size()));
            add.setStart_hour(24-i);
            add.setCategory(categories.get(i%categories.size()));
            add.setWeek(week.get(i%2));
            add.setAdditional_tag(tag.get(i));
            add.setRecommended("no");
            testSet.add(add);
        }

        List<ChaidAttr> attributes = new ArrayList<>();
        attributes.add(new ChaidAttr(days, DayAttr.class, trainingsData));
        attributes.add(new ChaidAttr(categories, CategoryAttr.class, trainingsData));
        attributes.add(new ChaidAttr(week, WeekAttr.class, trainingsData));
        attributes.add(new ChaidAttr(additionalTags, TagAttr.class, trainingsData));

        Chaid root = new Chaid(trainingsData, attributes);
        root.split();

        for(AlgorithmDTO event : events) {
            root.checkEvent(event);
        }

        double maxConfidence = 0;
        int cnt = 0;
        List<Long> confidentEventIds = new ArrayList<>();
        for(AlgorithmDTO event : events) {
            System.out.println(event.getName() + ": " + event.getConfidenceLevel());
            if (event.getConfidenceLevel() > maxConfidence) {
                maxConfidence = event.getConfidenceLevel();
                confidentEventIds = new ArrayList<>();
                confidenceCnt++;
            }
            if(event.getConfidenceLevel() == maxConfidence) {
                confidentEventIds.add(event.getId());
                cnt ++;
            }
        }
        System.out.println("Number of recommended events: " +cnt);
        System.out.println("Count of different confidence levels: " + confidenceCnt);
        return confidentEventIds;
    }
    public List < Long > applyCART(List < AlgorithmDTO > events, Instances data) throws Exception {
        int cnt = 0;
        int confidentCnt = 0;
        data.setClassIndex(data.numAttributes() - 1);
        String[] options = {
                "-U"
        };
        SimpleCart tree = new SimpleCart(); // new instance of tree
        tree.setOptions(options); // set the options
        tree.setMinNumObj(1);
        tree.setUsePrune(false);
        tree.buildClassifier(data); // build classifier
        Instances dataRaw = prepareData(events);
        for (int i = 0; i < dataRaw.numInstances(); i++) {
            double clsLabel = tree.classifyInstance(dataRaw.instance(i));
            double[] confidenceLevel = tree.distributionForInstance(dataRaw.instance(i));
            if (clsLabel == 0) {
                events.get(i).setRecommended("no");
                events.get(i).setConfidenceLvl(confidenceLevel[0]);
            }
            if (clsLabel == 1) {
                events.get(i).setRecommended("yes");
                events.get(i).setConfidenceLvl(confidenceLevel[1]);
            }
        }
        ArrayList < Long > ids = new ArrayList < Long > ();
        for (AlgorithmDTO event: events) {
            if (event.getRecommended() == "yes") {
                ids.add(event.getId());
                cnt++;
            }
        }
        return ids;
    }

    public Instances prepareData(List<AlgorithmDTO> events){
        ArrayList<Attribute> atts = new ArrayList<Attribute>(8);
        ArrayList<String> classVal = new ArrayList<String>();
        classVal.add("no");
        classVal.add("yes");
        ArrayList<String> day = new ArrayList<>();
        ArrayList<String> category = new ArrayList<>();
        ArrayList<String> week = new ArrayList<>();
        ArrayList<String> tag= new ArrayList<>();
        ArrayList<String> location = new ArrayList<>();

        day.addAll(Arrays.asList("Mon","Tue","Wed","Fri","Thu","Sat","Sun"));
        category.addAll(Arrays.asList("party","music","art","literature","comedy","food","games","health","shopping","home","sport","theatre","others"));
        week.addAll(Arrays.asList("workday","weekend"));
        tag.addAll(Arrays.asList("club","festival","concert","reading","cabaret","art_exhibit","streetfood","food","shopping","sport","late_night","football","running","musical","theatre","other","health","gala_dinner","classic","rock"));
        atts.add(new Attribute("Day", day));
        atts.add(new Attribute("Start Time", (int) 0));
        atts.add(new Attribute("Category",category));
        atts.add(new Attribute("Week",week));
        atts.add(new Attribute("additional_tag",tag));
        atts.add(new Attribute("Recommended",classVal));
        Instances dataRaw = new Instances("TestInstances",atts,0);
        dataRaw.setClassIndex(dataRaw.numAttributes() - 1);
        for(AlgorithmDTO event: events){
            double[] instanceValue1 = new double[dataRaw.numAttributes()];
            instanceValue1[0] = day.indexOf(event.getWeekday());
            instanceValue1[1] = event.getStart_hour();
            instanceValue1[2] = category.indexOf(event.getCategory().toLowerCase());
            instanceValue1[3] = week.indexOf(event.getWeek());
            instanceValue1[4] = tag.indexOf(event.getAdditional_tag());
            instanceValue1[5] = classVal.indexOf("yes");
            dataRaw.add(new DenseInstance(1.0, instanceValue1));
        }
        return dataRaw;
    }
    public Instances prepareTrainingSet(List<AlgorithmDTO> events){
        //Location is not relevant because it gets already filtered by the backend
        ArrayList<String> coveredTags = new ArrayList<String>();
        ArrayList<Attribute> atts = new ArrayList<Attribute>(8);
        ArrayList<String> classVal = new ArrayList<String>();
        classVal.add("no");
        classVal.add("yes");
        ArrayList<String> day = new ArrayList<>();
        ArrayList<String> category = new ArrayList<>();
        ArrayList<String> week = new ArrayList<>();
        ArrayList<String> tag= new ArrayList<>();
        //ArrayList<String> location = new ArrayList<>();

        day.addAll(Arrays.asList("Mon","Tue","Wed","Fri","Thu","Sat","Sun"));
        category.addAll(Arrays.asList("party","music","art","literature","comedy","food","games","health","shopping","home","sport","theatre","others"));
        week.addAll(Arrays.asList("workday","weekend"));
        tag.addAll(Arrays.asList("club","festival","concert","reading","cabaret","art_exhibit","streetfood","food","shopping","sport","late_night","football","running","musical","theatre","other","health","gala_dinner","classic","rock"));
        for(AlgorithmDTO event: events) {
            if (tag.indexOf(event.getAdditional_tag().toLowerCase()) != -1){
                if (!coveredTags.contains(event.getAdditional_tag().toLowerCase())){
                    coveredTags.add(event.getAdditional_tag().toLowerCase());
                }

            }
        }
        atts.add(new Attribute("Day", day));
        atts.add(new Attribute("Start Time", (int) 0));
        atts.add(new Attribute("Category",category));
        atts.add(new Attribute("Week",week));
        atts.add(new Attribute("additional_tag",tag));
        atts.add(new Attribute("Recommended",classVal));
        Instances dataRaw = new Instances("TestInstances",atts,0);
        dataRaw.setClassIndex(dataRaw.numAttributes() - 1);
        for(AlgorithmDTO event: events){
            double[] instanceValue1 = new double[dataRaw.numAttributes()];
            instanceValue1[0] = day.indexOf(event.getWeekday());
            instanceValue1[1] = event.getStart_hour();
            instanceValue1[2] = category.indexOf(event.getCategory().toLowerCase());
            instanceValue1[3] = week.indexOf(event.getWeek());
            instanceValue1[4] = tag.indexOf(event.getAdditional_tag());
            instanceValue1[5] = classVal.indexOf("yes");
            dataRaw.add(new DenseInstance(1.0, instanceValue1));
        }
        ArrayList<String> oldTags = new ArrayList<>(tag);

        for (int i = 0; i< coveredTags.size(); i ++){
            String actualTag = coveredTags.get(i);
            ArrayList<AlgorithmDTO> taggedEvents = new ArrayList<>();
            events.stream().filter(o -> o.getAdditional_tag().equals(actualTag)).forEach(
                    o -> {
                       taggedEvents.add(o);
                    });
            AlgorithmDTO reverse = taggedEvents.get(0);
            reverse = getPartOfWeek(reverse,taggedEvents,"weekend","workday");
            if (reverse.getRecommended().equals("no") ){
                double[] instanceValue1 = new double[dataRaw.numAttributes()];
                instanceValue1[0] = day.indexOf(reverse.getWeekday());
                instanceValue1[1] = reverse.getStart_hour();
                instanceValue1[2] = category.indexOf(reverse.getCategory().toLowerCase());
                instanceValue1[3] = week.indexOf(reverse.getWeek());
                instanceValue1[4] = tag.indexOf(reverse.getAdditional_tag());
                instanceValue1[5] = classVal.indexOf("no");
                dataRaw.add(new DenseInstance(1.0, instanceValue1));
            }
            reverse = taggedEvents.get(0);
            reverse = getTime(reverse,taggedEvents,15);
            if (reverse.getRecommended().equals("no") ){
                double[] instanceValue1 = new double[dataRaw.numAttributes()];
                instanceValue1[0] = day.indexOf(reverse.getWeekday());
                instanceValue1[1] = reverse.getStart_hour();
                instanceValue1[2] = category.indexOf(reverse.getCategory().toLowerCase());
                instanceValue1[3] = week.indexOf(reverse.getWeek());
                instanceValue1[4] = tag.indexOf(reverse.getAdditional_tag());
                instanceValue1[5] = classVal.indexOf("no");
                dataRaw.add(new DenseInstance(1.0, instanceValue1));
            }
            taggedEvents.clear();
        }
        tag.removeAll(coveredTags);
        for (int i = 0; i < tag.size(); i ++){
            double[] instanceValue1 = new double[dataRaw.numAttributes()];
            instanceValue1[0] = i%day.size();
            instanceValue1[1] = 24-i;
            instanceValue1[2] = i%category.size();
            instanceValue1[3] = i%2;
            instanceValue1[4] = oldTags.indexOf(tag.get(i));
            instanceValue1[5] = classVal.indexOf("no");
            dataRaw.add(new DenseInstance(1.0, instanceValue1));
        }
        return dataRaw;
    }
    public AlgorithmDTO getPartOfWeek(AlgorithmDTO ret, List<AlgorithmDTO> taggedEvents, String first, String second){
        ret.setRecommended("yes");
        if (taggedEvents.stream().filter(o -> o.getWeek().equals(first)).findFirst().isPresent()){
            if (taggedEvents.stream().filter(o -> o.getWeek().equals(second)).findFirst().isPresent() == false){
                ret.setWeekday("Mon");
                ret.setWeek("workday");
                ret.setRecommended("no");
            }
        }
        else if (taggedEvents.stream().filter(o -> o.getWeek().equals(second)).findFirst().isPresent()){
            if (taggedEvents.stream().filter(o -> o.getWeek().equals(first)).findFirst().isPresent() == false){
                ret.setWeekday("Sat");
                ret.setWeek("weekend");
                ret.setRecommended("no");
            }
        }
        return ret;
    }

    public AlgorithmDTO getTime(AlgorithmDTO ret, List<AlgorithmDTO> taggedEvents, int startHour){
        ret.setRecommended("yes");
        if (taggedEvents.stream().filter(o -> o.getStart_hour() >= startHour).findFirst().isPresent()){
            if (taggedEvents.stream().filter(o -> o.getStart_hour() < startHour).findFirst().isPresent() == false){
                ret.setStart_hour(startHour - 1);
                ret.setRecommended("no");
            }
        }
        else if (taggedEvents.stream().filter(o -> o.getStart_hour() < startHour).findFirst().isPresent()){
            if (taggedEvents.stream().filter(o -> o.getStart_hour() >= startHour).findFirst().isPresent() == false){
                ret.setStart_hour(startHour);
                ret.setRecommended("no");
            }
        }
        return ret;
    }

    public List<AlgorithmDTO> convertEventData(List<EventEntity> events) {
        List<AlgorithmDTO> data = new ArrayList<>();
        for(EventEntity event : events){
            if(event != null) {
                AlgorithmDTO add = new AlgorithmDTO();
                add.setStart_time_converted(event.getStarttime());
                add.setCategory(event.getCategory().name());
                add.setCity(event.getCity());
                add.setOperator(event.getPlaceName());
                add.setName(event.getName());
                add.setId(event.getId());
                Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                calendar.setTime(add.getStart_time_converted());   // assigns calendar to given date
                add.setStart_hour(calendar.get(Calendar.HOUR_OF_DAY)); // gets hour in 24h format
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                String dayStr;
                String weekStr;
                switch (day) {
                    case 1:
                        dayStr = "Sun";
                        weekStr = "weekend";
                        break;
                    case 2:
                        dayStr = "Mon";
                        weekStr = "workday";
                        break;
                    case 3:
                        dayStr = "Tue";
                        weekStr = "workday";
                        break;
                    case 4:
                        dayStr = "Wed";
                        weekStr = "workday";
                        break;
                    case 5:
                        dayStr = "Thu";
                        weekStr = "workday";
                        break;
                    case 6:
                        dayStr = "Fri";
                        weekStr = "weekend";
                        break;
                    case 7:
                        dayStr = "Sat";
                        weekStr = "weekend";
                        break;
                    default:
                        dayStr = "undefined";
                        weekStr = "undefined";
                        break;

                }
                String description = "";
                if (event.getDescription() != null) {
                    description = event.getDescription().toLowerCase();
                }
                String tag = "";
                switch (add.getCategory().toLowerCase()) {
                    case "party":
                        tag = "club";
                        break;
                    case "music":
                        if (description.contains("philharmon") || description.contains("orchester")) {
                            tag = "classic";
                        } else if (description.contains("rock")) {
                            tag = "rock";
                        } else {
                            tag = "concert";
                        }
                        break;
                    case "art - culture":
                        add.setCategory("art");
                        tag = "art_exhibit";
                        break;
                    case "literatur":
                        add.setCategory("literature");
                        tag = "reading";
                        break;
                    case "comedy":
                        tag = "cabaret";
                        break;
                    case "food":
                        if (description.contains("streetfood")) {
                            tag = "streetfood";
                        }
                        else if (description.contains("dinner")) {
                            tag = "gala_dinner";
                        }
                        else{
                            tag = "food";
                        }

                        break;
                    case "health":
                        tag = "health";
                        break;
                    case "shopping":
                        tag = "shopping";
                        break;
                    case "home_garden":
                        add.setCategory("home");
                        tag = "shopping";
                        break;
                    case "sport":
                        if (description.contains("fußball")) {
                            tag = "football";
                        }
                        else if (description.contains("marathon")) {
                            tag = "running";
                        } else {
                            tag = "sport";
                        }
                        break;
                    case "theatre":
                        if (description.contains("musical")) {
                            tag = "musical";
                        } else {
                            tag = "theatre";
                        }
                        break;
                    default:
                        tag = "other";
                        break;
                }
                add.setAdditional_tag(tag);
                add.setWeek(weekStr);
                add.setWeekday(dayStr);
                data.add(add);
            }
        }
        return data;
    }
}
