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
    ArrayList < InetAddress > stripeIPs = new ArrayList < InetAddress > ();
    ConcurrentHashMap < String, Integer > fileSizes = new ConcurrentHashMap < String, Integer > ();
    ConcurrentHashMap < String, String > authCodes = new ConcurrentHashMap < String, String > ();
    ConcurrentLinkedDeque < SocketChannel > socketQueue = new ConcurrentLinkedDeque < SocketChannel > ();
    boolean isActive;
    InetSocketAddress daemonIP;
    ServerSocketChannel serverSocket;
    InetSocketAddress commandIP;
    int defaultBufferSize;
    boolean active = false;
    String[] tierLocations;
    int timeOut;
    StorageDaemon(boolean isActive, InetSocketAddress daemonIP, InetSocketAddress commandIP, int defaultBufferSize, String[] tierLocations, int maxActiveThreads,int timeOut) {
        this.isActive = isActive;
        this.daemonIP = daemonIP;
        this.commandIP = commandIP;
        this.defaultBufferSize = defaultBufferSize;
        this.tierLocations = tierLocations;
        this.maxActiveThreads=maxActiveThreads;
        serverSocket.bind(daemonIP);
        this.timeOut=timeOut;
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
        ThreadPoolExecutor executorService = ThreadPoolExecutor.newCachedThreadPool();
        //ArrayList<SocketChannel> socketChannels= new ArrayList<SocketChannel>();
        //int index;
        Runnable recieve = () -> {
            SocketChannel socket=socketQueue.pollFirst();
            final Thread currentThread = Thread.currentThread();
            Runnable priority = () -> {
                int counter;
                Thread t = Thread.currentThread();
                t.setPriority(1);
                while(true){
                    counter++;
                     if(counter==30){
                        currentThread.setPriority(7);
                    }
                    else if(counter==60){
                        currentThread.setPriority(10);
                        t.interrupt();
                        return;
                    }
                    long millis=10;
                    t=Thread.sleep(millis);
                }
            };
            byte[] commandBytes;
            ByteBuffer buffer=ByteBuffer.allocate(defaultBufferSize);//change, just guesswork
            byte[] temp= new byte[1];
            temp[1]=0;
            buffer=ByteBuffer.wrap(temp);
            socket.write(buffer);
            buffer.clear();
            buffer.flip();
            int responseWait=0;
            currentThread.setPriority(1);
            do{
                socket.read(buffer);
                if(responseWait<timeOut){
                    responseWait++;
                    Thread.sleep(1);
                }
                else{
                    return;
                }
            }
            while(responseWait<=timeOut&&buffer!=null);
            buffer.clear();
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(priority);
            buffer.get(command, 0, 75);//first 75 bytes are metadata
            String[] commandComponents = command.toString().split(":");// 0 is command,1 is name, 2 is tier, 3 is authkey
            Integer tierObject=Integer.parseInt(commandComponent[2]);
            int tier=tierObject;
            if (commandComponents[0].equals("get")) {
                buffer.clear();
                if (Integer.toHexString(components[2].hashCode()) == authCodes.get(components[0])) {
                    RandomAccessFile file = new RandomAccessFile(tier + "/" + components[0] + ".dtrec", "r");
                    buffer = ByteBuffer.allocate(fileSizes.get(components[0]));
                    FileChannel fileChannel = file.getChannel();
                    int bytesRead = fileChannel.read(bb);
                    buffer.flip();
                    InetSocketAddress remote = new InetSocketAddress(InetAddress.getByName(components[2]));
                    socket.write(buffer);
                } else {
                    return;
                }
            } else if (commandComponents[0].equals("set")) {
                RandomAccessFile file = new RandomAccessFile(tierLocations[tier] = "/" + components[0] + ".dtrec", "w");
                authCodes.add(Integer.toHexString(components[2].hashCode()));
                /****************************************************************************/
                //This section recieves the actual data from the client
                /****************************************************************************/
                //This section verifies whether the recieved data is the data associated with the right sender
                buffer.position(75);
                buffer=buffer.slice();
                /****************************************************************************/
                buffer.flip();
                FileChannel channel = file.getChannel();
                channel.write(buffer);
                fileSizes.put(key, buffer.position());
                if (isActive) {
                    SocketChannel activeShareSocketChannel=SocketChannel.open();
                    for (int i = 1; i < stripeIps.size(); i++) {
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
            if(socketQueue.length!=0&&executorService.getActiveCount()<maxActiveThreads){
                executorService.execute(recieve);
            }
            else{
                continue;
            }
        }
    }
}