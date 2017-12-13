package tech.pod.dataset;
import java.io.File;
import java.io.IOException;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.channels.FileChannel.MapMode;
public class BinaryStreamParser < T > {
    ReentrantLock pauseLock;
    ReentrantLock stopLock;
    String globalLogger;
    BinaryStreamParser(ReentrantLock pauseLock, ReentrantLock stopLock, String...globalLogger) {
        this.pauseLock = pauseLock;
        this.stopLock = stopLock;
        if (globalLogger.length != 0) {
            this.globalLogger = globalLogger[0];
        }
    }

    public List < T > binaryStreamDecode(int bufferLength, String type, Class < T > caster, String tempName, List < T > output) {
        Logger logger = null;
        if (globalLogger != null) {
            logger = Logger.getLogger(globalLogger);
            logger.entering(globalLogger, "binaryStreamDecode()");
        } else {
            logger = Logger.getLogger(ImportThread.class.getName());
            logger.entering(getClass().getName(), "binaryStreamDecode()");
        }

        while (!stopLock.isLocked()) {

            File f = new File(tempName, "rw");
            FileChannel channel;
            try {
                channel = FileChannel.open(f.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
                MappedByteBuffer b = channel.map(MapMode.READ_WRITE, 0, bufferLength);
                if (!pauseLock.isLocked()) {
                    if (type == "string") {
                        CharBuffer charBuffer = b.asCharBuffer();
                        char[] array = charBuffer.array();
                        String str2 = array.toString();
                        String[] strs = str2.split("//");
                        for (String i: strs) {
                            T a = caster.cast(i);
                            output.add(a);
                            output.clear();
                            //return output;
                        }
                    }
                    if (type == "num") {
                        DoubleBuffer doubleBuffer = b.asDoubleBuffer();
                        double[] array = doubleBuffer.array();
                        for (double i: array) {
                            T a = caster.cast(i);
                            output.add(a);
                            output.clear();
                            //return output;

                        }
                    }
                }
            } catch (IOException e) {
                logger.logp(Level.WARNING, "BinaryStreamParser", "binaryStreamDecode()", "IOException", e);
            }




        }


        return null;
    }
}