package tech.pod.dataset;
import java.io.File;
import java.io.IOException;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.*;
import java.nio.channels.FileChannel.MapMode;
public class BinaryStreamParser < T > {
    ReentrantLock pauseLock;
    ReentrantLock stopLock;
    BinaryStreamParser(ReentrantLock pauseLock, ReentrantLock stopLock) {
        this.pauseLock = pauseLock;
        this.stopLock = stopLock;
    }

    public List < T > binaryStreamDecode(int bufferLength,String type, Class < T > caster) {
        while (!stopLock.isLocked()) {
            List < T > output = new ArrayList < T > ();
            File f = new File("temp", "rw");
            FileChannel channel;
            try {
                channel = FileChannel.open(f.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
                MappedByteBuffer b = channel.map(MapMode.READ_WRITE, 0, bufferLength);
                if (!pauseLock.isLocked()) {
                    if (type == "string") {
                        CharBuffer charBuffer = b.asCharBuffer();
                        char[] array = charBuffer.array();
                        String str = array.toString();
                        String[] strs = str.split("//");
                        for (String i: strs) {
                            T a = caster.cast(i);
                            output.add(a);
                            return output;
                        }
                    }
                    if (type == "num") {
                        DoubleBuffer doubleBuffer = b.asDoubleBuffer();
                        double[] array = doubleBuffer.array();
                        for (double i: array) {
                            T a = caster.cast(i);
                            output.add(a);
                            return output;

                        }
                    }
                }
            } catch (IOException e) {
                //TODO: handle exception
            }




        }


        return null;
    }
}