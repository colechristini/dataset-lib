package tech.pod.dataset.storageprovider;

import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//StorageDaemons run on individual servers, managing the files on the server and replicationg them to all servers within the stripe.
public class StorageDaemon {
    int maxActiveThreads;
    ArrayList < InetSocketAddress > stripeIPs = new ArrayList < InetSocketAddress > ();
    ConcurrentHashMap < String, Integer > fileSizes = new ConcurrentHashMap < String, Integer > ();
    ConcurrentHashMap < String, String > authCodes = new ConcurrentHashMap < String, String > ();
    ConcurrentLinkedDeque < SocketChannel > socketQueue = new ConcurrentLinkedDeque < SocketChannel > ();
    boolean isActive;
    InetSocketAddress daemonIP;
    ServerSocketChannel serverSocket;
    int defaultBufferSize;
    boolean active = false;
    String[] tierLocations;
    int timeOut;
    int threadMaxCompleteTime;
    StorageDaemon(boolean isActive, InetSocketAddress daemonIP, int defaultBufferSize, String[] tierLocations, int maxActiveThreads, int timeOut, int threadMaxCompleteTime) {
        this.isActive = isActive;
        this.daemonIP = daemonIP;
        this.defaultBufferSize = defaultBufferSize;
        this.tierLocations = tierLocations;
        this.maxActiveThreads = maxActiveThreads;
        serverSocket.bind(daemonIP);
        this.timeOut = timeOut;
        this.threadMaxCompleteTime=threadMaxCompleteTime;
    }

    public void start() {
        active = true;
    }

    public void pause() {
        active = false;
    }

    public void unpause() {
        active = true;
    }

    public void recieve() {
        ConcurrentHashMap < String, ByteBuffer > datamap = new ConcurrentHashMap < String, ByteBuffer > ();
        RejectedExecutionHandlerImpl rejectedExecutionHandlerImpl=new RejectedExecutionHandlerImpl();
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(2, maxActiveThreads, threadMaxCompleteTime, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2), Executors.defaultThreadFactory(), rejectedExecutionHandlerImpl);
        //ArrayList<SocketChannel> socketChannels= new ArrayList<SocketChannel>();
        //int index;
        Runnable recieve = () -> {
            SocketChannel socket = socketQueue.pollFirst();
            final Thread currentThread = Thread.currentThread();
            Runnable priority = () -> {
                int counter=0;
                Thread t = Thread.currentThread();
                t.setPriority(1);
                while (true) {
                    counter++;
                    if (counter == 30) {
                        currentThread.setPriority(7);
                    } else if (counter == 60) {
                        currentThread.setPriority(10);
                        t.interrupt();
                        return;
                    }
                    long millis = 10;
                    Thread.sleep(millis);
                }
            };
            byte[] commandBytes = new byte[75];
            ByteBuffer buffer = ByteBuffer.allocate(defaultBufferSize); //change, just guesswork
            byte[] temp = new byte[1];
            temp[1] = 0;
            buffer = ByteBuffer.wrap(temp);
            socket.write(buffer);
            buffer.clear();
            buffer.flip();
            int responseWait = 0;
            currentThread.setPriority(1);
            do {
                socket.read(buffer);
                if (responseWait < timeOut) {
                    responseWait++;
                    Thread.sleep(1);
                } else {
                    return;
                }
            }
            while (responseWait <= timeOut && buffer != null);
            buffer.clear();
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(priority);
            buffer.get(commandBytes, 0, 75); //first 75 bytes are metadata
            String[] commandComponents = commandBytes.toString().split(":"); // 0 is command,1 is name, 2 is tier, 3 is authcode
            Integer tierObject = Integer.parseInt(commandComponents[2]);
            int tier = tierObject;
            if (commandComponents[0].equals("get")) {
                buffer.clear();
                if (Integer.toHexString(commandComponents[3].hashCode()) == authCodes.get(commandComponents[1])) {
                    RandomAccessFile file = new RandomAccessFile(commandComponents[2] + "/" + commandComponents[1] + ".dtrec", "r");
                    buffer = ByteBuffer.allocate(fileSizes.get(commandComponents[1]));//change to config option for aways using default buffer size
                    FileChannel fileChannel = file.getChannel();
                    int bytesRead = fileChannel.read(buffer);
                    file.close();
                    buffer.flip();
                    socket.write(buffer);
                } else {
                    return;
                }
            } else if (commandComponents[0].equals("set")) {
                RandomAccessFile file = new RandomAccessFile(tierLocations[(int)Integer.parseInt(commandComponents[2])] = "/" + commandComponents[1] + ".dtrec", "w");
                authCodes.put(commandComponents[1],Integer.toHexString(commandComponents[3].hashCode()));
                /****************************************************************************/
                //This section recieves the actual data from the client
                /****************************************************************************/
                //This section verifies whether the recieved data is the data associated with the right sender
                buffer.position(75);
                buffer = buffer.slice();
                /****************************************************************************/
                buffer.flip();
                FileChannel channel = file.getChannel();
                file.close();
                channel.write(buffer);
                fileSizes.put(commandComponents[1], buffer.position());
                if (isActive) {
                    SocketChannel activeShareSocketChannel = SocketChannel.open();
                    for (int i = 1; i < stripeIPs.size(); i++) {
                        activeShareSocketChannel.connect(stripeIPs.get(i));
                        activeShareSocketChannel.finishConnect();
                        socket.write(buffer);
                        activeShareSocketChannel.close();
                    }
                }
            }
        };
        while (active) {
            SocketChannel socket = serverSocket.accept();
            if (socket != null) {
                socketQueue.add(socket);
            }
            if (socketQueue.size() != 0 && executorService.getActiveCount() < maxActiveThreads) {
                executorService.execute(recieve);
            } else {
                continue;
            }
        }
    }
}