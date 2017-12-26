package tech.pod.dataset.ims;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchAgent {
    List < List < String >> searchGroup;
    List < IndexKey > searchOutput;
    SearchAgent(List < List < String >> searchGroup, List < IndexKey > searchOutput) {
        this.searchGroup = searchGroup;
        this.searchOutput = searchOutput;
    }
    public List < IndexKey > search() {
     
        DateFormat format = new SimpleDateFormat("   yyyy.MM.dd  HH:mm:ss z", Locale.ENGLISH);
        if (searchGroup.get(0).get(0) != "any") {
            List < DateClassifier > dateClassifiers = new ArrayList < DateClassifier > ();
            for (int i=0;i<searchGroup.get(0).length;i++) {
                dateClassifiers.add(new DateClassifier(searchGroup.get(0).get(i).split("-")[1],"after",  searchGroup.get(0).get(i).split("-")[0], searchOutput));//need to format first parameter
                searchOutput=dateClassifiers.get(i).classify();
            }
        }
        if (searchGroup.get(1).get(0) != "any") {
            List < DateClassifier > dateClassifiers = new ArrayList < DateClassifier > ();
            for (int i=0;i<searchGroup.get(1).length;i++) {
                dateClassifiers.add(new DateClassifier(searchGroup.get(1).get(i).split("-")[1],"after",  searchGroup.get(1).get(i).split("-")[0], searchOutput));//need to format first parameter
                searchOutput=dateClassifiers.get(i).classify();
            }
        }
        if (searchGroup.get(2).get(0) != "any") {
            List<StringClassifier> stringClassifiers=new ArrayList<StringClassifier>();
            for (int i=0;i<searchGroup.get(1).length;i++) {
               stringClassifiers.add(new StringClassifier(searchGroup.get(1).get(i).split("-")[1],searchGroup.get(1).get(i).split("-")[0],searchOutput));
            }
        }
        return searchOutput;
    }
}