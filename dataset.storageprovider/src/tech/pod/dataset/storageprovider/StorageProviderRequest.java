package tech.pod.dataset.storageprovider;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class StorageProviderRequest {
    SocketChannel channel;
    ByteBuffer content;

    StorageProviderRequest(SocketChannel channel, ByteBuffer content) {
        this.channel = channel;
        this.content = content;
    }

    public ByteBuffer getContent() {
        return content;
    }

    public SocketChannel getChannel() {
        return channel;
    }
}
