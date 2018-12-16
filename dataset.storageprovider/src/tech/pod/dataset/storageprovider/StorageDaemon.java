package tech.pod.dataset.storageprovider;

import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.KeyStore;
import java.security.PublicKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.security.KeyStore.SecretKeyEntry;

//StorageDaemons run on individual servers, managing the files on the server and replicationg them to all servers within the stripe.
public class StorageDaemon {
    int maxActiveThreads;
    ArrayList<InetSocketAddress> stripeIPs = new ArrayList<InetSocketAddress>();
    ConcurrentHashMap<String, Integer> fileSizes = new ConcurrentHashMap<String, Integer>();
    ConcurrentHashMap<String, String> authCodes = new ConcurrentHashMap<String, String>();
    ConcurrentLinkedDeque<SocketChannel> socketQueue = new ConcurrentLinkedDeque<SocketChannel>();
    boolean isActive;
    InetSocketAddress daemonIP;
    ServerSocketChannel serverSocket;
    int defaultBufferSize;
    boolean active = false;
    String[] tierLocations;
    int timeOut;
    int threadMaxCompleteTime;
    KeyStore salt = KeyStore.getInstance(KeyStore.getDefaultType());

    StorageDaemon(boolean isActive, InetSocketAddress daemonIP, int defaultBufferSize, String[] tierLocations,
            int maxActiveThreads, int timeOut, int threadMaxCompleteTime) {
        this.isActive = isActive;
        this.daemonIP = daemonIP;
        this.defaultBufferSize = defaultBufferSize;
        this.tierLocations = tierLocations;
        this.maxActiveThreads = maxActiveThreads;
        try {
            serverSocket.bind(daemonIP);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.timeOut = timeOut;
        this.threadMaxCompleteTime = threadMaxCompleteTime;
        File keyStore = new File(tierLocations[0] + "/KeyStore.jks");
        if (keyStore.exists()) {
            try {
                InputStream stream = new FileInputStream(keyStore);
                salt.load(stream,
                        Integer.toHexString(InetAddress.getLocalHost().getHostName().hashCode()).toCharArray());
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            try {
                salt.load(null, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(
                    Integer.toHexString(InetAddress.getLocalHost().getHostName().hashCode()).toCharArray());
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // for example
            SecretKey secretKey = keyGen.generateKey();
            salt.setEntry("salt", new SecretKeyEntry(secretKey), protParam);
        }
    }

    public void start() {
        active = true;
    }

    public void stop() {
        active = false;
    }

    public void recieve() {
        RejectedExecutionHandlerImplementation rejectedExecutionHandlerImpl=new RejectedExecutionHandlerImplementation();
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(2, maxActiveThreads, threadMaxCompleteTime, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2), Executors.defaultThreadFactory(), rejectedExecutionHandlerImpl);
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
                    try {
                    Thread.sleep(millis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            byte[] commandBytes = new byte[75];
            ByteBuffer buffer = ByteBuffer.allocate(defaultBufferSize); //change, just guesswork
            int responseWait = 0;
            currentThread.setPriority(1);
            do {
                try{
                socket.read(buffer);
                }
                catch(IOException e){
                    e.printStackTrace();
                }
                if (responseWait < timeOut) {
                    responseWait++;
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
            if (commandComponents[0].equals("get")) {
                buffer.clear();
                if(commandComponents.length==3){
                    SecretKey saltKey=salt.getKey("salt", Integer.toHexString(InetAddress.getLocalHost().getHostName()).hashCode());
                    String auth=commandComponents[3]+saltKey.getEncoded().toString();
                    if (Integer.toHexString(auth.hashCode()) == authCodes.get(commandComponents[1]))) {
                        buffer = ByteBuffer.allocate(fileSizes.get(commandComponents[1]));//change to config option for always using default buffer size
                        try{
                            RandomAccessFile file = new RandomAccessFile(tierLocations[(int)Integer.parseInt(commandComponents[2])] + "/" + commandComponents[1] + ".dtrec", "r");
                            FileChannel fileChannel = file.getChannel();
                            int bytesRead = fileChannel.read(buffer);
                            file.close();
                            buffer.flip();
                            socket.write(buffer);
                        }
                        catch(IOException e){
                            e.printStackTrace();
                        }
                    } else {
                        return;
                    }
                }
                else{
                    try{
                        RandomAccessFile file = new RandomAccessFile(tierLocations[(int)Integer.parseInt(commandComponents[2])] + "/" + commandComponents[1] + ".dtrec", "r");
                        FileChannel fileChannel = file.getChannel();
                        int bytesRead = fileChannel.read(buffer);
                        file.close();
                        buffer.flip();
                        socket.write(buffer);
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }
            } else if (commandComponents[0].equals("put")) {
                try {
                    RandomAccessFile file = new RandomAccessFile(tierLocations[(int)Integer.parseInt(commandComponents[2])] = "/" + commandComponents[1] + ".dtrec", "w");
                    SecretKey saltKey=salt.getKey("salt", Integer.toHexString(InetAddress.getLocalHost().getHostName()).hashCode());
                    String auth=commandComponents[3]+saltKey.getEncoded().toString();
                    authCodes.put(commandComponents[1],Integer.toHexString(auth.hashCode()));
                    buffer.position(75);
                    buffer = buffer.slice();
                    buffer.flip();
                    FileChannel channel = file.getChannel();
                    try {
                        file.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        channel.write(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fileSizes.put(commandComponents[1], buffer.position());
                    if (isActive) {
                        try {
                            SocketChannel activeShareSocketChannel = SocketChannel.open();
                            for (int i = 1; i < stripeIPs.size(); i++) {
                                try {
                                    activeShareSocketChannel.connect(stripeIPs.get(i));
                                    activeShareSocketChannel.finishConnect();
                                    socket.write(buffer);
                                    activeShareSocketChannel.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        };
        while (active) {
            
            try {
                SocketChannel socket= serverSocket.accept();
                if (socket != null) {
                    socketQueue.add(socket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
           
            if (socketQueue.size() != 0 && executorService.getActiveCount() < maxActiveThreads) {
                executorService.execute(recieve);
            } else {
                continue;
            }
        }
    }
}