package tech.pod.dataset.io;

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
/*StreamThread is standalone Callable thread taking data over a ServerSocketChannel,
and communicating with a parser using a MappedByteBuffer
*/
public class StreamThread implements Callable < ByteBuffer > {

    ByteBuffer buff;
    int port;
    String tempName;
    int bufferSize;
    ReentrantLock stop,pause;
    int sync;
    StreamThread(int port, int bufferSize, ReentrantLock stop, ReentrantLock pause, String tempName, int sync) {
        this.sync = sync;
        this.port = port;
        this.tempName = tempName;
        this.bufferSize = bufferSize;
        this.stop = stop;
        this.pause = pause;
    }

    public ByteBuffer call() throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        buff = ByteBuffer.allocate(bufferSize);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        RandomAccessFile temp = new RandomAccessFile(tempName, "rw");
        MappedByteBuffer b;
        while (!stop.isLocked()) {
            sync=0;
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.read(buff);
            FileChannel channel = temp.getChannel();
            channel.write(buff);
            if (!pause.isLocked()) {
                b = channel.map(MapMode.READ_WRITE, 0, (long) bufferSize);
                sync = 1;
                if(sync==2){
                b.clear();
                }
            }
            buff.clear();
        }
        temp.close();
        return null;
    }


}