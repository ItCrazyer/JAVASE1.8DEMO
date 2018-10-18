package server;

import service.Protocol;
import service.Service;
import java.io.*;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


//server的主函数
//主要负责打开tcp监听请求
//然后对请求做处理
//
public class ServerMain {
    public static void main(String[] args) throws IOException {

        //使用30个线程池
        ExecutorService executorService = Executors.newFixedThreadPool(30);

        ServerSocket serverSocket = new ServerSocket();

        //绑定本地端口8000
        serverSocket.bind(new InetSocketAddress(8000));
        while (true)
        {
            Socket socket = serverSocket.accept();
            //获得socket连接后逻辑处理放入线程池中
            //由子线程处理，main线程则进入下一个循环进行监听
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    //java7新增的自动关闭资源的括号语法
                    try(
                            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
                            )
                    {
                        Protocol protocol = (Protocol)in.readObject();

                        Method method = Service.class.getDeclaredMethod(protocol.funname,(Class<?>[]) protocol.parametertype);
                        Object result = method.invoke(new ServiceImp(),protocol.args);
                        Protocol responseProtocol = new Protocol();
                        responseProtocol.result = result;

                        out.writeObject(responseProtocol);

                        socket.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }




                }
            });



        }




    }
}
