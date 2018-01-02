package tech.pod.dataset.storageprovider;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.io;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;



import javafx.scene.shape.Path;

public class S3Provider implements StorageProvider {
    boolean acceptingConnections;
     String bucketName;
    int maxAgents;
    S3Provider(String bucketName, int maxAgents,boolean acceptingConnections) {
        this.bucketName = bucketName;
        this.maxAgents=maxAgents;
        this.acceptingConnections = acceptingConnections;
    }
    public void start(int port,int paramBufferSize,int maxFileSize) {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        ByteBuffer parameters=ByteBuffer.allocate(paramBufferSize);
        ByteBuffer content=ByteBuffer.allocate(maxFileSize);
        CharBuffer buffer=CharBuffer.allocate(paramBufferSize);
        List<ScheduledFuture> futures=new ArrayList<ScheduledFuture>();
        ExecutorService executorService=Executors.newFixedThreadPool(maxAgents);
        int i;
        Runnable serverThread= () -> {
            socketChannel.read(parameters);
            if (params[0].equals("put")) {
                socketChannel.read(content);
            }
            buffer = buff.asCharBuffer();
            String str = buffer.toString();
            String[] params = str.split(":");
            if (params[0].equals("put")) {
                Path p = Paths.get(params[2]);
                Files.write(p, content.array());
                this.put(params);
            }
           else if (params[0].equals("get")) {
               ByteBuffer b=this.get(params);
            socketChannel.write(b);
        }
        else if (params[0].equals("remove")) {
            this.remove(params);
            }
            futures.remove(i);
        };
        while (acceptingConnections) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            if(socketChannel!=null) { 
                ScheduledFuture future=executorService.submit(serverThread);
                i=futures.size();
                futures.add(future);
            }
    }
}
public void remove(Object[] params){
    AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());        
    s3client.deleteObject(new DeleteObjectRequest(bucketName, params[1]));
}
    public Object get(Object[] params){
        AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());        
        S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, params[1]));
        InputStream objectData = object.getObjectContent();
        byte[] bytes=IOUtils.toByteArray(is);
        ByteBuffer b=ByteBuffer.wrap(bytes);
        return b;
    }
    public void put(Object[] params) {
        String key = (String) params[1];
        File file = new File(params[1]);
        AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());
        try {
            s3client.putObject(new PutObjectRequest(bucketName, key, file));
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
            "means your request made it " +
            "to Amazon S3, but was rejected with an error response" +
            " for some reason.");
            System.out.println("Error Message:" + ase.getMessage());
            System.out.println("HTTP Status Code:" + ase.getStatusCode());
            System.out.println("AWS Error Code:" + ase.getErrorCode());
            System.out.println("Error Type:" + ase.getErrorType());
            System.out.println("Request ID:" + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
            "means the client encountered " +
            "an internal error while trying to " +
            "communicate with S3, " +
            "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
}