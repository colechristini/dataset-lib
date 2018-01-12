package tech.pod.dataset.io;
import java.util.concurrent.Callable;
import java.io.RandomAccessFile;
import java.nio.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;

//ImportManager is a basic manager for a List of ImportThreads allowing for rapid, multithreaded import of files from disk.
public class ImportManager implements Callable < ByteBuffer >, Manager {
    RandomAccessFile file;
    int threadPoolSize;
    String globalLogger;
    ImportManager(RandomAccessFile file, int threadPoolSize, String globalLogger) {
        this.globalLogger = globalLogger;
        this.file = file;
        this.threadPoolSize = threadPoolSize;
    }
    public ByteBuffer call() throws Exception {
        Logger logger = null;
        if (globalLogger != null) {
            logger = Logger.getLogger(globalLogger);
            logger.entering(globalLogger, "call()");

        } else {
            logger = Logger.getLogger(ImportThread.class.getName());
            logger.entering(getClass().getName(), "call()");

        }

        ExecutorService exec = Executors.newFixedThreadPool(threadPoolSize);
        int byteBufferLength = (int) file.length() / threadPoolSize;
        Callable < ByteBuffer > [] thread = new ImportThread[threadPoolSize];
        List < Future < ByteBuffer > > Futures = new ArrayList < Future < ByteBuffer > > ();
        for (int i = 0; i < threadPoolSize - 1; i++) {
            thread[i] = new ImportThread(file, byteBufferLength, byteBufferLength * i);
            Future < ByteBuffer > future = exec.submit(thread[i]);
            Futures.add(future);
        }
        thread[threadPoolSize - 1] = new ImportThread(file, byteBufferLength + 1, byteBufferLength * threadPoolSize - 1);
        ByteBuffer[] bufs = new ByteBuffer[threadPoolSize];

        for (int i = 0; i < threadPoolSize - 1; i++) {
            bufs[i] = ByteBuffer.allocate(byteBufferLength);
            bufs[i] = Futures.get(i).get();
        }
        bufs[threadPoolSize] = ByteBuffer.allocate(byteBufferLength + 1);
        bufs[threadPoolSize] = Futures.get(threadPoolSize).get();
        int a = 0;
        while (a < threadPoolSize) {
            for (int i = 0; i < threadPoolSize; i++) {
                if (Futures.get(i).isDone()) {
                    a++;
                }
            }
        }
        ByteBuffer bufcol = ByteBuffer.allocate((int) file.length() + 1);
        if (a == threadPoolSize - 1) {
            for (ByteBuffer i: bufs) {
                bufcol.put(i);
            }


        }
        logger.logp(Level.FINE, "", "call()", "Import complete");
        return bufcol;

    }
}