package tech.pod.dataset.storageprovider;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class CommandRequest{
    SocketChannel channel;
    ByteBuffer content;
    CommandRequest(SocketChannel channel, ByteBuffer content){
        this.channel=channel;
        this.content=content;
    }
    CommandRequest(){}
    public SocketChannel getChannel(){
        return channel;
    }
    public ByteBuffer getBuffer(){
        return content;
    }
}