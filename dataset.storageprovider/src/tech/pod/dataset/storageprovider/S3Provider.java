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

//S3Provider acts as a basic StorageProvider, uploading, getting, and removing objects from Amazon AWS S3

public class S3Provider implements StorageProvider {
    boolean acceptingConnections;
    String bucketName;
    String tempPath;
    InetSocketAddress daemonIP;
    ServerSocket command=Socket.open();
    InetSocketAddress commandIP;
    int defaultBufferSize;
    String saveLocation;
    boolean running=false;
    S3Provider(String bucketName, String saveLocation,boolean isActive,InetSocketAddress daemonIP,InetSocketAddress commandIP,int defaultBufferSize) {
            this.bucketName = bucketName;
            this.saveLocation=saveLocation;
            this.isActive=isActive;
            this.daemonIP=daemonIP;
            this.commandIP=commandIP;
            this.defaultBufferSize=defaultBufferSize;
            this.saveLocation=saveLocation;
    }
    public void start(){
        command.bind(commandIP);
        running=true;
    }
    public void pause(){
        running=false;
    }
    public void unpause(){
        running=true;
    }
    public void recieve(){
        List<Socket> sockets=new ArrayList<Socket>();
        ExecutorService executorService=Executors.newCachedThreadPool();
        Runnable recieve=()->{
            PrintWriter out=new PrintWriter(sockets.get(sockets.size()-1).getOutputStream(),true);
            String commands=out.toString();
            String commandString=commands;
                if(commandString==get){
                    String[] components=commandString.split(":");
                    RandomAccessFile file=new RandomAccessFile(fileNames.get(components[1]),"r");
                    ByteBuffer buffer=ByteBuffer.allocate(fileSizes.get(components[1]));
                    FileChannel fileChannel=file.getChannel();
                    int bytesRead=fileChannel.read(bb);
                    buffer.flip();
                    InetSocketAddress remote=new InetSocketAddress(InetAddress.getByName(components[2]));
                    SocketChannel socket=SocketChannel.open();
                    socket.bind(daemonIP);
                    socket.connect(remote);
                    socket.finishConnect();
                    socket.write(buffer);
                }
                else if(commandString==write){
                    String[] components=commandString.split(":");
                    RandomAccessFile file=new RandomAccessFile(saveLocation="/"+components[2]+".dtrec","w");
                    fileLocs.put(components[1], components[2]);
                    ByteBuffer buffer=ByteBuffer.allocate(defaultBufferSize);
                    InetSocketAddress remote=new InetSocketAddress(InetAddress.getByName(components[3]));
                    SocketChannel socket=SocketChannel.open();
                    socket.bind(daemonIP);
                    socket.connect(remote);
                    socket.read(buffer);
                    buffer.flip(); 
                    FileChannel channel=file.getChannel();
                    channel.write(buffer);
                    if(isActive){
                        for(int i=1;i<stripeIps.size();i++){
                            Socket commandSenderSocket=new Socket(stripeIPs.get(i),10000);
                            OutputStream stream=commandSenderSocket.getOutputStream();
                            PrintWriter writer=new PrintWriter(stream);
                            writer.write(commandString);
                            socket.connect(stripeIPs.get(i));
                            socket.write(buffer);
                        }
                    }
                }
        };
       while(running){
        Socket socket=command.accept();
        sockets.add(socket);
           if(socket!=null){
            executorService.execute(recieve);
           }
       }
    }
    public void remove(Object[] params) {
        AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());
        try {
            s3client.deleteObject(new DeleteObjectRequest(bucketName, params[0].toString()));
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
    public Object get(String file) {
        AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
        try {
            S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, file));
            InputStream objectData = object.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(is);
            ByteBuffer b = ByteBuffer.wrap(bytes);
            return b;
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
    public void write(String fileKey, File f) {
        String key = fileKey;
        File file = f;
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