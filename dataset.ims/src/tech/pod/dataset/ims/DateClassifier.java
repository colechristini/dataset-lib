package tech.pod.dataset.ims;

import java.util.List;
import java.util.concurrent.Future;

public class DateClassifier {
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

    public List < T > classify() {
        int syncheck = 0;
        final List < IndexKey > temp = classifierOutput;
        Callable < List < IndexKey > > classifierCall = () -> {
            List < IndexKey > innerTemp = temp;
            if (mode == "after") {
                if (date == "lastAccess") {
                    for (int i = 0; i < classifierOutput.length; i++) {
                        if (!innerTemp.get(i).getLastAccessTime().after(check)) {
                            innerTemp.remove(i);
                        }
                    }
                } else if (date == "import") {
                    for (int i = 0; i < classifierOutput.length; i++) {
                        if (!innerTemp.get(i).getImportTime().after(check)) {
                            innerTemp.remove(i);
                        }
                    }
                } else if (date == "creation") {
                    for (int i = 0; i < classifierOutput.length; i++) {
                        if (!innerTemp.get(i).getCreationTime().after(check)) {
                            innerTemp.remove(i);
                        }
                    }
                }
            } else if (mode == "before") {
                if (date == "lastAccess") {
                    for (int i = 0; i < classifierOutput.length; i++) {
                        if (!innerTemp.get(i).getLastAccessTime().before(check)) {
                            innerTemp.remove(i);
                        }
                    }
                } else if (date == "import") {
                    for (int i = 0; i < classifierOutput.length; i++) {
                        if (!innerTemp.get(i).getImportTime().before(check)) {
                            innerTemp.remove(i);
                        }
                    }
                } else if (date == "creation") {
                    for (int i = 0; i < classifierOutput.length; i++) {
                        if (!innerTemp.get(i).getCreationTime().before(check)) {
                            innerTemp.remove(i);
                        }
                    }
                }

            }
            syncheck++;
            return innerTemp;
        };
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future < List < IndexKey >> future = service.submit(classifierCall);
        if (syncheck == 1) {
            classifierOutput = future.get();
            return classifierOutput;
        }
    }
}