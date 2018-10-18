package client;

import server.ServiceImp;
import service.Protocol;
import service.Service;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;

public class DynamicProxyFactory {


    public static Object getProxy()
    {
        return Proxy.newProxyInstance(Service.class.getClassLoader(), ServiceImp.class.getInterfaces(),new Handler());
    }

    static private class Handler implements InvocationHandler
    {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {

            Object result = null;
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("127.0.0.1", 8000));
                Protocol protocol = new Protocol(method.getName(),method.getParameterTypes(),args,null);

                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                out.writeObject(protocol);
                Protocol responseProtocol = (Protocol)in.readObject();
                socket.close();
                result = responseProtocol.result;
            }
            catch (Exception e){e.printStackTrace();}


            return result;
        }
    }

}
