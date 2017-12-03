package tech.pod.dataset;

import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

public class StreamThread implements Callable < ByteBuffer > {

    ByteBuffer buff;
    int port;
    int status;
    int bufferSize;
    ReentrantLock stop,
    pause;
    StreamThread(int port, int status, int bufferSize, ReentrantLock stop, ReentrantLock pause) {

        this.port = port;
        this.status = status;
        this.bufferSize = bufferSize;
        this.stop = stop;
        this.pause = pause;
    }

    public ByteBuffer call() throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        buff = ByteBuffer.allocate(bufferSize);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        while (!stop.isLocked()) {
            RandomAccessFile temp = new RandomAccessFile("./temp.txt", "rw");
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.read(buff);
            FileChannel channel = temp.getChannel();
            channel.write(buff);
            if (!pause.isLocked()) {
                MappedByteBuffer b = channel.map(MapMode.READ_WRITE, 0, (long) bufferSize);
            }
            temp.close();
        }

        return null;
    }


}