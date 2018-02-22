package tech.pod.dataset.faas;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
    public void accept() {
        Runnable addFunction = () -> {
           
            int location = sockets.size() - 1;
            Socket s=sockets.get(location);
            ObjectInputStream stream = s.getInputStream();
            Object[] objects = stream.readObject();
            sockets.remove(location);
            Thread thread = Thread.currentThread();
            Class < InvokeableFunction > caster = new Class < InvokeableFunction > ();
            InvokeableWrapper wrapper = new InvokeableWrapper(caster.cast(objects[1]), objects[2].toString());
            sockets.remove(s);
        };
        Runnable removeFunction = () -> {
            int location = sockets.size() - 1;
            Socket s=sockets.get(location);
            ObjectInputStream stream =  s.getInputStream();
            Object[] objects = stream.readObject();
            Thread thread = Thread.currentThread();
            if (functions.get(objects[1].toString()).getKeyLocation(objects[2].toString()) != 0) {
                functions.remove(objects[1].toString());
                sockets.remove(s);
            }
            else{
                sockets.remove(s);
                throw new KeyNotAuthorizedException(objects[2].toString());
            }
            
        };
        Runnable functionCall = () -> {
            int location = sockets.size() - 1;
            Socket s=sockets.get(location);
            ObjectInputStream stream = s.getInputStream();
            Object[] objects = stream.readObject();
            Thread thread = Thread.currentThread();
            try {
                List < ? extends Object > returns;
                if (objects.length >= 5) {
                    Object[] extraparams = Arrays.copyOfRange(objects, 4, objects.length - 1);
                    returns = functions.get(objects[1].toString()).invoke(objects[2].toString, objects[3], extraparams);
                } else {
                    returns = functions.get(objects[1].toString()).invoke(objects[2].toString, objects[3]);
                }

                ObjectOutputStream output = s.getOutputStream();
                output.writeObject(returns);
                sockets.remove(s);
            } catch (KeyNotAuthorizedException kna) {

            }
        };
        while (!stop) {
            Socket tempSocket = socket.accept();
            if (tempSocket != null) {
                sockets.add(tempSocket);
                ExecutorService service = Executors.newSingleThreadExecutor();
                ObjectInputStream stream = sockets.get(location).getInputStream();
                Object[] objects = stream.readObject();

                if (objects[0].toString() == "invoke") {
                    service.execute(functionCall);
                }
            
                else if (objects[0].toString() == "add") {
                service.execute(addFunction);
                }

                else if (objects[0].toString() == "remove") {
                    service.execute(removeFunction);
            }
        }
    }
    stop = false;
}

}