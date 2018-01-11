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
                if (date == "lastAccess") {
                    for (int i = 0; i < classifierOutput.length; i++) {
                        if (!classifierOutput.get(i).getLastAccessTime().after(check)) {
                            classifierOutput.remove(i);
                        }
                    }
                } else if (date == "import") {
                    for (int i = 0; i < classifierOutput.length; i++) {
                        if (!classifierOutput.get(i).getImportTime().after(check)) {
                            classifierOutput.remove(i);
                        }
                    }
                } else if (date == "creation") {
                    for (int i = 0; i < classifierOutput.length; i++) {
                        if (!classifierOutput.get(i).getCreationTime().after(check)) {
                            classifierOutput.remove(i);
                        }
                    }
                }
            } else if (mode == "before") {
                if (date == "lastAccess") {
                    for (int i = 0; i < classifierOutput.length; i++) {
                        if (!classifierOutput.get(i).getLastAccessTime().before(check)) {
                            classifierOutput.remove(i);
                        }
                    }
                } else if (date == "import") {
                    for (int i = 0; i < classifierOutput.length; i++) {
                        if (!classifierOutput.get(i).getImportTime().before(check)) {
                            classifierOutput.remove(i);
                        }
                    }
                } else if (date == "creation") {
                    for (int i = 0; i < classifierOutput.length; i++) {
                        if (!classifierOutput.get(i).getCreationTime().before(check)) {
                            classifierOutput.remove(i);
                        }
                    }
                }

            }
            return classifierOutput;
        
        
    }
}