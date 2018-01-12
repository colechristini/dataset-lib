package tech.pod.dataset.ims;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//WordHistogramClassifier classifies an IndexKey list based on whether an individual IndexKey's WordHistogramList contains the queried words in the given quantities
public class WordHistogramClassifier implements ClassifierRule < IndexKey > {
    List < IndexKey > classifierOutput;
    List < String > wordKeys;
    HashMap < String,
    Integer > wordClassifiers;
    WordHistogramClassifier(List < String > wordKeys, HashMap < String, Integer > wordClassifiers, List < IndexKey > classifierInput) {
        this.wordKeys = wordKeys;
        this.wordClassifiers = wordClassifiers;
        classifierOutput = classifierInput;
    }
    public List < IndexKey > classify() {
        for (int i = 0; i < wordKeys.length; i++) {
            Integer individualWordCount = wordClassifiers.get(wordKeys.get(i));
            for (int j = 0; j < classifierOutput.length; j++) {
                HashMap wordOccurences = classifierOutput.get(i).wordOccurences();
                if(wordOccurences.get(wordKeys.get(i))==null||(Integer)wordOccurences.get(wordKeys.get(i))<individualWordCount){
                    classifierOutput.remove(j);
                    continue;
                }
            }
        }
        return classifierOutput;
    }
}