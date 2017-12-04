package tech.pod.dataset;
import java.nio.*;
import java.util.concurrent.locks.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
public class StreamManager {
    ReentrantLock pauseLock;
    ReentrantLock stopLock;
    StreamThread thread;

    StreamManager(int port, int status, int bufferSize) {
        thread = new StreamThread(port, bufferSize, stopLock, pauseLock);
    }
    public void start() {
        if (stopLock.isLocked()) {
            stopLock.unlock();
        }
        ExecutorService exec = Executors.newFixedThreadPool(1);
        Callable < ByteBuffer > callableThread = thread;
        List < Future < ByteBuffer > > Futures = new ArrayList < Future < ByteBuffer > > ();
        Future < ByteBuffer > future = exec.submit(callableThread);
        Futures.add(future);
    }
    public void pause() {
        if (pauseLock.isLocked()) {
            pauseLock.unlock();
        } else {
            pauseLock.lock();
        }

    }
    public void stop() {
        stopLock.lock();
    }

}