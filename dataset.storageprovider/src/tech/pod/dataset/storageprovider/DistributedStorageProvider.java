package tech.pod.dataset.storageprovider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
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
//DistributedStorageProvider acts as a more advanced on-premises StorageProvider, saving, getting, and removing objects from a set of distributed StoragePools

public class DistributedStorageProvider implements StorageProviderInterface {
    ConcurrentHashMap<String, Boolean> heartbeatMap = new ConcurrentHashMap<String, Boolean>();
    List<StoragePoolInterface> storagePools = new ArrayList<StoragePoolInterface>();
    ConcurrentHashMap<String, StorageKey> files = new ConcurrentHashMap<String, StorageKey>();
    InetSocketAddress ip;
    int timeOut;
    int threadMaxCompleteTime;
    int maxActiveThreads;
    ServerSocketChannel serverSocket;
    int maxHeartbeatThreads;
    boolean active;
    int pool, stripe = 0;
    int defaultBufferSize;

    DistributedStorageProvider(InetSocketAddress ip, int maxActiveThreads, int maxHeartbeatThreads, int timeOut,
            int threadMaxCompleteTime, int defaultBufferSize) {
        this.ip = ip;
        this.maxActiveThreads = maxActiveThreads;
        this.maxHeartbeatThreads = maxHeartbeatThreads;
        try {
            serverSocket.bind(ip, 9999);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.timeOut = timeOut;
        this.threadMaxCompleteTime = threadMaxCompleteTime;
        this.defaultBufferSize = defaultBufferSize;
    }

    DistributedStorageProvider(InetSocketAddress ip, int port, int timeOut, int maxActiveThreads,
            int maxHeartbeatThreads, int threadMaxCompleteTime, int defaultBufferSize) {
        this.ip = ip;
        this.maxActiveThreads = maxActiveThreads;
        try {
            serverSocket.bind(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.timeOut = timeOut;
        this.threadMaxCompleteTime = threadMaxCompleteTime;
        this.defaultBufferSize = defaultBufferSize;
    }

    public void start() {
        active = true;
    }

    public void stop() {
        active = false;
    }

    public void addPool(String mode) {
        if (mode == "homogenous") {
            storagePools.add(new HomogenousPool());
        } else if (mode == "heterogenous") {
            storagePools.add(new HeterogenousPool());
        }
    }

    public void addPool(String mode, List<List<InetSocketAddress>> presuppliedDaemons, List<Integer> tiers) {
        if (mode == "homogenous") {
            storagePools.add(new HomogenousPool());
        } else if (mode == "heterogenous") {
            storagePools.add(new HeterogenousPool());
        }
        if (storagePools.get(storagePools.size() - 1) instanceof HomogenousPool) {
            int a = 0;
            for (int i = 0; i < presuppliedDaemons.size(); i++) {
                a++;
                storagePools.get(storagePools.size() - 1).addStripe(
                        presuppliedDaemons.get(i).toArray(new InetSocketAddress[presuppliedDaemons.get(i).size()]));
                for (int inc = 0; inc < presuppliedDaemons.get(i).size(); inc++) {
                    heartbeatMap.put(presuppliedDaemons.get(i).get(inc).toString(), (Boolean) true);
                }
            }
        } else if (storagePools.get(storagePools.size() - 1) instanceof HeterogenousPool) {
            for (int i = 0; i < presuppliedDaemons.size(); i++) {
                storagePools.get(storagePools.size() - 1).addStripe(
                        presuppliedDaemons.get(i).toArray(new InetSocketAddress[presuppliedDaemons.get(i).size()]),
                        (int) tiers.get(i));
                for (int inc = 0; inc < presuppliedDaemons.get(i).size(); i++) {
                    heartbeatMap.put(presuppliedDaemons.get(i).get(inc).toString(), (Boolean) true);
                }
            }
        }
    }

    public void startHeartbeat(int port, int acceptablePing, long heartbeatTimer, TimeUnit unit) {
        Runnable heartbeat = () -> {
            ConcurrentLinkedDeque<Socket> sockets = new ConcurrentLinkedDeque<Socket>();
            ConcurrentLinkedDeque<int[]> poolStripe = new ConcurrentLinkedDeque<int[]>();
            Runnable reciever = () -> {
                int[] poolStripeArray = poolStripe.pollFirst();
                Socket socket = sockets.pollFirst();
                int currentPing = 0;
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    int pool = poolStripeArray[0];
                    int stripe = poolStripeArray[1];
                    while (currentPing < acceptablePing) {
                        String str = reader.readLine();
                        currentPing++;
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (str != null) {
                            return;
                        } else {
                            continue;
                        }
                    }
                    StoragePoolInterface storagePool = storagePools.get(pool);
                    storagePool.incrementRepLayer(stripe);
                    heartbeatMap.replace(storagePools.get(pool).getDaemon(stripe).toString(), false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            ArrayList<ArrayList<InetSocketAddress>> temp = new ArrayList<ArrayList<InetSocketAddress>>(
                    heartbeatMap.size());
            for (int i = 0; i < storagePools.size(); i++) {
                ArrayList<InetSocketAddress> converter = storagePools.get(i).getAllDaemons();
                temp.add(converter);
            }
            ExecutorService service = Executors.newFixedThreadPool(maxHeartbeatThreads);
            for (int i = 0; i < storagePools.size(); i++) {
                final int pool1 = i;
                for (int a = 0; a < storagePools.get(i).getAllDaemons().size(); a++) {
                    final int stripe1 = a;
                    Socket socket = new Socket();
                    try {
                        socket.bind(ip);
                        socket.connect(storagePools.get(i).getDaemon(a));
                        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                        writer.write("ping");
                        sockets.addFirst(socket);
                        int[] poolStripeArray = { pool1, stripe1 };
                        poolStripe.addLast(poolStripeArray);
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (sockets.size() < maxHeartbeatThreads) {
                        Future<?> f = service.submit(reciever, null);
                    }
                }
            }
        };
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> future = service.scheduleWithFixedDelay(heartbeat, heartbeatTimer, heartbeatTimer, unit);
    }

    public void startRecieve(String port) {
        int currentpool = 0;
        int currentstripe = 0;
        RejectedExecutionHandlerImplementation rejectedExecutionHandlerImpl = new RejectedExecutionHandlerImplementation();
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(2, maxActiveThreads, threadMaxCompleteTime,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2), Executors.defaultThreadFactory(),
                rejectedExecutionHandlerImpl);
        ConcurrentLinkedDeque<SocketChannel> socketQueue = new ConcurrentLinkedDeque<SocketChannel>();
        Runnable recieve = () -> {
            SocketChannel socket = socketQueue.pollFirst();
            final Thread currentThread = Thread.currentThread();
            Runnable priority = () -> {
                int counter = 0;
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
            ByteBuffer buffer = ByteBuffer.allocate(defaultBufferSize); // change, just guesswork
            byte[] temp = new byte[1];
            temp[1] = 0;
            buffer = ByteBuffer.wrap(temp);
            try {
                socket.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            CharBuffer charBuffer = buffer.asCharBuffer();
            String command = charBuffer.toString();
            String[] commandComponents = command.split(":");
            if (commandComponents[0].equals("get")) {
                stripe++;
                if (stripe > storagePools.get(pool).getStripeCount()) {
                    pool++;
                    stripe = 0;
                }
                StorageKey key = (StorageKey) get(commandComponents[1]);
                int[] path = key.getPath();
                InetSocketAddress daemon = storagePools.get(path[0]).getDaemon(path[1]);
                buffer.clear();
                buffer = ByteBuffer.wrap(daemon.getAddress().getAddress());
                buffer.flip();
                try {
                    socket.write(buffer);
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            } else if (commandComponents[0].equals("put")) {
                InetSocketAddress daemon = put(commandComponents[1], commandComponents[2]);
                buffer.clear();
                buffer = ByteBuffer.wrap(daemon.getAddress().getAddress());
                buffer.flip();
                try {
                    socket.write(buffer);
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            } else if (commandComponents[0].equals("remove")) {
                StorageKey key = (StorageKey) get(commandComponents[1]);
                int[] path = key.getPath();
                InetSocketAddress daemon = storagePools.get(path[0]).getDaemon(path[1]);
                buffer.clear();
                buffer = ByteBuffer.wrap(daemon.getAddress().getAddress());
                buffer.flip();
                try {
                    socket.write(buffer);
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                remove(commandComponents[1]);
                return;
            }
        };
        executorService.submit(recieve);
        while (active) {
            try {
                SocketChannel socket = serverSocket.accept();
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

    public InetSocketAddress put(String objectName, String tier) {
        stripe++;
        if (stripe == storagePools.get(pool).getStripeCount()) {
            stripe = 0;
            pool++;
        }
        if (storagePools.get(pool) instanceof HeterogenousPool) {
            HeterogenousPool currentPool = (HeterogenousPool) storagePools.get(pool);
            Integer temp = Integer.parseInt(tier);
            StorageKey key = new StorageKey(pool, stripe, objectName, Integer.toHexString(objectName.hashCode()));
            files.put(objectName, key);
            return currentPool.getDaemonByTier(temp.intValue());
        } else {
            StorageKey key = new StorageKey(pool, stripe, objectName, Integer.toHexString(objectName.hashCode()));
            files.put(objectName, key);
            return storagePools.get(pool).getDaemon(stripe);
        }
    }

    public void put(String objectName, Object object) {
        return;
    }

    public Object get(String objectName) {
        return files.get(objectName);
    }

    public void remove(String objectName) {
        files.remove(objectName);
    }

}