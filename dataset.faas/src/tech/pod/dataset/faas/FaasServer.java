package tech.pod.dataset.faas;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;


public class FaasServer {
    ConcurrentHashMap < String, InvokeableWrapper > functions = new ConcurrentHashMap < String, InvokeableWrapper > ();
    ServerSocket socket;
    List < Socket > sockets = new ArrayList < Socket > ();
    int port;
    boolean stop;
    FaasServer(int port) {
        this.port = port;
        socket = new ServerSocket(port);
    }
    public void accept(){
        Callable functionCall=()->{
            int location=sockets.size()-1;
            ObjectInputStream stream= sockets.get(location).getInputStream();
            Object[] objects=stream.readObject();
            try{
                Object[] extraparams=Arrays.copyOfRange(objects, 3, objects.length-1);
               List<? extends Object> returns=functions.get(objects[0].toString()).invoke(objects[1].toString, objects[2], extraparams) ;
            }
            catch(KeyNotAuthorizedException kne){

            }
        };
        while(!stop){

        }
        stop=false;
    }
    
}