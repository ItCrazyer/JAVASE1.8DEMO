package server;

import resolver.DefiniteLengthParser;
import resolver.TempData;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

public class TCPNioServer {


    public static void main(String[] args) throws Exception {


        openServer(new InetSocketAddress(3000));
    }



    private static void openServer(InetSocketAddress inetSocketAddress) throws Exception {
        Charset charset = Charset.forName("utf-8");

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        Selector selector = Selector.open();

        serverSocketChannel.bind(inetSocketAddress);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);



        while (selector.select() > 0)
        {
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            SocketChannel sc = null;
                while (iterator.hasNext()) {
                    SelectionKey sk = iterator.next();
                    iterator.remove();
                    if(sk.isAcceptable())
                    {
                        //sc = ((ServerSocketChannel)sk.channel()).accept();这个和下面一样
                        sc = serverSocketChannel.accept();
                        sc.configureBlocking(false);
                        //下面把sk给新的sk，新的sk代表SocketChannel的sk，而之前的sk是ServerSocketChannel的sk
                        sk = sc.register(selector,SelectionKey.OP_READ);
                        DefiniteLengthParser.sendBytes(sk,("服务端收到你的连接请求，连接已建立!!!").getBytes(charset));

                    }
                    else if(sk.isReadable())
                    {
                        try {
                            DefiniteLengthParser.getBytes(sk);

                            TempData tempData = (TempData)sk.attachment();
                            while (!tempData.queue.isEmpty()) {
                                String content = new String(tempData.queue.poll(),charset);
                                System.out.println("客户端传来的消息:" + content);
                            }
                            DefiniteLengthParser.sendBytes(sk,("服务器收到了你的消息!").getBytes(charset));
                        }
                        catch (Exception e)
                        {
                            System.out.println(e.getMessage());
                                sk.cancel();
                                if (sc != null)
                                    sc.close();

                        }

                    }
                }





        }

        System.out.println("程序退出!");

    }


}
