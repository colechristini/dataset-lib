package tech.pod.dataset.io;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
/*ScheduledStreamCache is a cache allowing for flow coming from a constant StreamOutput
to be batched and flushed at regular intervals or with user input to allow for batch operations on the set*/
public class ScheduledStreamCache < T > {
    String globalLogger;
    List < T > output;
    List < T > internal = new ArrayList < T > ();
    Logger logger = null;
    ReentrantLock pauseLock,
    stopLock;
    ScheduledStreamCache(List < T > output, ReentrantLock pauseLock, ReentrantLock stopLock, String...globalLogger) {
        this.output = output;
        if (globalLogger.length != 0) {
            this.globalLogger = globalLogger[0];
        }

        if (this.globalLogger != null) {
            logger = Logger.getLogger(this.globalLogger);
            logger.entering(this.globalLogger, "binaryDecode()");
        } else {
            logger = Logger.getLogger(ScheduledStreamCache.class.getName());
            logger.entering(getClass().getName(), "binaryDecode()");
        }
        this.pauseLock = pauseLock;
        this.stopLock = stopLock;
    }
    List < T > flush() {
        internal.clear();
        logger.logp(Level.INFO, "StreamCache", "flush()", "Flushed cache");
        return internal;
    }

    void add(List < T > toAdd) {
        internal.addAll(toAdd);
        logger.logp(Level.INFO, "StreamCache", "add()", "Added List<T> to internal List");
    }

    void add(T toAdd) {
        internal.add(toAdd);
        logger.logp(Level.INFO, "StreamCache", "add()", "Added T to internal List");
    }

    void start(int tm, List < T > Output) {
        int a = 0;
        while (!stopLock.isLocked()) {
            try {
                if (!pauseLock.isLocked()) {
                    for (int i = 0; i < tm; i++) {
                        a--;
                        TimeUnit.MILLISECONDS.sleep(1);
                        if (a == 0) {
                            output.addAll(flush());
                            a = tm;
                        }
                    }

                }
            } catch (InterruptedException ie) {
                logger.logp(Level.INFO, "StreamCache", "start()", "Interrupted sleep cycle", ie);
            }
        }

    }
}