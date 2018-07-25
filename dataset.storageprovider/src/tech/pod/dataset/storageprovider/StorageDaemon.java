package tech.pod.dataset.storageprovider;

import java.nio.*;
import java.nio.channels.FileChannel;
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
    ConcurrentLinkedDeque < Socket > socketQueue = new ConcurrentLinkedDeque < Socket > ();
    boolean isActive;
    InetSocketAddress daemonIP;
    ServerSocket command = Socket.open();
    InetSocketAddress commandIP;
    int defaultBufferSize;
    boolean active = false;
    String[] tierLocations;

    StorageDaemon(boolean isActive, InetSocketAddress daemonIP, InetSocketAddress commandIP, int defaultBufferSize, String[] tierLocations, int maxActiveThreads) {
        this.isActive = isActive;
        this.daemonIP = daemonIP;
        this.commandIP = commandIP;
        this.defaultBufferSize = defaultBufferSize;
        this.tierLocations = tierLocations;
        this.maxActiveThreads=maxActiveThreads;
    }

    public void start() {
        command.bind(commandIP);
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
        ThreadPoolExecutor executorService = Executors.newCachedThreadPool();
        Runnable recieve = () -> {
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
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(priority);
            PrintWriter out = new PrintWriter(socketQueue.poll().getOutputStream(), true);
            String commands = out.toString();
            String commandString = commands;
            String[] components = commandString.split(":");// 0 is name,1 is authKey
            if (commandString.contains("get")) {
                if (Integer.toHexString(components[2].hashCode()) == authCodes.get(components[0])) {
                    RandomAccessFile file = new RandomAccessFile(fileNames.get(tierLocations[components[1]] = "/" + components[0] + ".dtrec"), "r");
                    ByteBuffer buffer = ByteBuffer.allocate(fileSizes.get(components[0]));
                    FileChannel fileChannel = file.getChannel();
                    int bytesRead = fileChannel.read(bb);
                    buffer.flip();
                    InetSocketAddress remote = new InetSocketAddress(InetAddress.getByName(components[2]));
                    SocketChannel socket = SocketChannel.open();
                    socket.bind(daemonIP);
                    socket.connect(remote);
                    socket.finishConnect();
                    socket.write(buffer);
                } else {
                    return;
                }
            } else if (commandString.contains("write")) {
                Integer a=Integer.parseInt(components[1]);
                int tier=a;
                RandomAccessFile file = new RandomAccessFile(tierLocations[tier] = "/" + components[0] + ".dtrec", "w");
                authCodes.add(Integer.toHexString(components[2].hashCode()));
                String token=components[3];
                /****************************************************************************/
                //This section recieves the actual data from the client
                ByteBuffer buffer = ByteBuffer.allocate(defaultBufferSize);
                InetSocketAddress remote = new InetSocketAddress(InetAddress.getByName(components[3]));
                SocketChannel socket = SocketChannel.open();
                socket.bind(daemonIP);
                socket.connect(remote);
                socket.read(buffer);
                /****************************************************************************/
                //This section verifies whether the recieved data is the data associated with the right sender
                byte[] data;
                buffer.get(data, 0, 1);
                Byte bt=data[0];
                buffer.position(2);
                datamap.put(bt.toString(), buffer.slice());
                buffer.clear();
                buffer.put(datamap.get(token));
                /****************************************************************************/
                buffer.flip();
                FileChannel channel = file.getChannel();
                channel.write(buffer);
                fileSizes.put(key, buffer.position());
                if (isActive) {
                    for (int i = 1; i < stripeIps.size(); i++) {
                        Socket commandSenderSocket = new Socket(stripeIPs.get(i), 10000);
                        OutputStream stream = commandSenderSocket.getOutputStream();
                        PrintWriter writer = new PrintWriter(stream);
                        writer.write(commandString);
                        socket.connect(stripeIPs.get(i));
                        socket.write(buffer);
                    }
                }
            }
        };
        while (active) {
            Socket socket = command.accept();
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