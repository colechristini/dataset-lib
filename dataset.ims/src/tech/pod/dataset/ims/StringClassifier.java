package tech.pod.dataset.ims;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//StringClassifier is a ClassifierRule using a regex on either the title, the tags, or the hashcode of a list of IndexKeys
public class StringClassifier < IndexKey > implements ClassifierRule < IndexKey > {
    List < IndexKey > classifierOutput;
    String regex,mode;
    public StringClassifier(String regex, String mode, List < IndexKey > classifierInput) {
        this.regex = regex;
        this.mode = mode;
        classifierOutput = classifierInput;
    }
    public List < IndexKey > classify() {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;
    
            for (int i = 0; i < classifierOutput.length; i++) {
                matcher = pattern.matcher(classifierOutput.get(i).getProperty(mode));
                if (matcher.find() == false) {
                    classifierOutput.remove(i);
                }
            }
        return classifierOutput;
    }
}