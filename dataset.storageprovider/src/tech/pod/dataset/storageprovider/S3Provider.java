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
    String tempPath;
    S3Provider(Object[] params) {
        if (params.length !=3) {
            throw new UnsupportedOperationException();
        } else {
            this.bucketName = params[0].toString();
            this.maxAgents = (int)params[1];
            tempPath=params[2].toString();
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
    public Object get(Object[] params) {
        AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
        try {
            S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, params[0].toString()));
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
    public void put(Object[] params) {
        String key = params[0].toString();
        File file = new File(params[0].toString());
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