package tech.pod.dataset;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class StreamOutput < T > {
    ReentrantLock pauseLock;
    ReentrantLock stopLock;
    BinaryStreamParser < T > b;
    List < T > output;
@SafeVarargs
    StreamOutput(ReentrantLock pauseLock, ReentrantLock stopLock, BinaryStreamParser < T > b, List < T > ...output) {
        this.b = b;
        this.pauseLock = pauseLock;
        this.stopLock = stopLock;
        if (output.length != 0) {
            this.output = output[0];
        }
    }

    public void output(List < T > sharedList) {

        while (!stopLock.isLocked() && !pauseLock.isLocked()) {
            output.addAll(sharedList);
        }
        if (stopLock.isLocked()) {
            return;
        }
    }

    public void output(StreamCache < T > c, List < T > sharedList) {
        while (!stopLock.isLocked() && !pauseLock.isLocked()) {
            c.add(sharedList);
        }
        if (stopLock.isLocked()) {
            return;
        }
    }

    public void output(ScheduledStreamCache < T > sc, List < T > sharedList) {
        while (!stopLock.isLocked() && !pauseLock.isLocked()) {
            sc.add(sharedList);
        }
        if (stopLock.isLocked()) {
            return;
        }
    }


}