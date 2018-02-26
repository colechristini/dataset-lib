package tech.pod.dataset.ims;

import java.util.List;
import java.util.concurrent.Future;
//DateClassifier with support for both before and after operations on LastAccessTime, import, and creation time for the IndexKey
public class DateClassifier implements ClassifierRule<IndexKey>{
    List < IndexKey > classifierOutput;
    String mode;
    Date check;
    String date;
    public DateClassifier(Date check, String mode, String date, List < IndexKey > classifierInput) {
        this.check = check;
        this.mode = mode;
        classifierOutput = classifierInput;
        this.date = date;
    }

    public List < IndexKey > classify() {
            if (mode == "after") {
              
                    for (int i = 0; i < classifierOutput.length; i++) {
                        if (!classifierOutput.get(i).getProperty(mode).after(check)) {
                            classifierOutput.remove(i);
                        }
                    }
                }
             
            else if (mode == "before") {
               
                    for (int i = 0; i < classifierOutput.length; i++) {
                        if (!classifierOutput.get(i).getProperty(mode).before(check)) {
                            classifierOutput.remove(i);
                        }
                    }
                

            }
            return classifierOutput;
        
        
    }
}