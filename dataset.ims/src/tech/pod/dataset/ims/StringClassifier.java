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
        if (mode == "title") {
            for (int i = 0; i < classifierOutput.length; i++) {
                matcher = pattern.matcher(classifierOutput.get(i).getTitle());
                if (matcher.find() == false) {
                    classifierOutput.remove(i);
                }
            }
        }
       else if (mode == "tags") {
            for (int i = 0; i < classifierOutput.length; i++) {
                matcher = pattern.matcher(classifierOutput.get(i).getTags());
                if (matcher.find() == false) {
                    classifierOutput.remove(i);
                }
            }
        }
        else if (mode == "hashcode") {
            for (int i = 0; i < classifierOutput.length; i++) {
                matcher = pattern.matcher(classifierOutput.get(i).getHashCode());
                if (matcher.find() == false) {
                    classifierOutput.remove(i);
                }
            }
        }
        return classifierOutput;
    }
}