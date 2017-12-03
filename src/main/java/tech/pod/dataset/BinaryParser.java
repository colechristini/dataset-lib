package tech.pod.dataset;

import java.nio.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class BinaryParser < T > {
    ByteBuffer buffer;
    String globalLogger;
    BinaryParser(ByteBuffer buffer,String... globalLogger) {
        if(globalLogger[0]!=null){
            this.globalLogger=globalLogger[0];
        }
        this.buffer = buffer;
    }
    public List < T > binaryDecode(String type, Class < T > temp) {
        List < T > Output = new ArrayList < T > ();
        Logger logger = null;
        if (globalLogger != null) {
            logger=Logger.getLogger(globalLogger);
        } else {
            logger=Logger.getLogger(BinaryParser.class.getName());
        }
        logger.entering(getClass().getName(), "binaryDecode()");
        if (type == "string") {
            logger.logp(Level.FINE, "BinaryParser", "binaryDecode", "beginning decode");
            CharBuffer charBuffer = CharBuffer.allocate(buffer.position());
            charBuffer = buffer.asCharBuffer();
            char[] arr = charBuffer.array();
            String str = arr.toString();
            String[] strs = str.split("//");
            for (String i: strs) {
                T a = temp.cast(i);
                Output.add(a);


            }
        } else if (type == "num") {

            DoubleBuffer doubleBuffer = DoubleBuffer.allocate((int) buffer.position() / 8);
            doubleBuffer = buffer.asDoubleBuffer();
            double[] d = doubleBuffer.array();
            for (double i: d) {
                T a = temp.cast(i);
                Output.add(a);

            }

        }
        logger.logp(Level.FINE, "BinaryParser", "binaryDecode", "completed decode");
        return Output;
        
    }
}