package tech.pod.dataset;
import java.util.concurrent.Callable;
import java.io.RandomAccessFile;
import java.nio.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.List;
import java.util.ArrayList;

//import java.util.concurrent.*;
public class MultiThreadedImport implements Callable < ByteBuffer > {
    RandomAccessFile file;
    int threadPoolSize;
    MultiThreadedImport(RandomAccessFile file, int threadPoolSize) {
        this.file = file;
        this.threadPoolSize = threadPoolSize;
    }
    public ByteBuffer call() throws Exception {
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
        return bufcol;
    }
}