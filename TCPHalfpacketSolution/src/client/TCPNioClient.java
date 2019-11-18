package client;

import resolver.DefiniteLengthParser;
import resolver.TempData;


import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;


public class TCPNioClient {

    public static void main(String[] args) throws Exception{

        for (int i = 0; i < 100;i++)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        openclient(new InetSocketAddress(3000));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();





    }


    private static void openclient(InetSocketAddress inetSocketAddress) throws Exception {
        Charset charset = Charset.forName("utf-8");

        Selector selector = Selector.open();
        SocketChannel channel = SocketChannel.open(inetSocketAddress);
        channel.configureBlocking(false);
        channel.register(selector,SelectionKey.OP_READ);

        while (selector.select() > 0)
        {
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {

                SelectionKey sk = iterator.next();
                iterator.remove();
                SocketChannel sc = (SocketChannel) sk.channel();
                //SocketChannel sc = channel; 这个和上面的一样
                if(sk.isReadable())
                {

                    DefiniteLengthParser.getBytes(sk);
                    TempData tempData = (TempData) sk.attachment();

                    while (!tempData.queue.isEmpty()) {
                        String content = new String(tempData.queue.poll(),charset);
                        System.out.println(Thread.currentThread().getName() + ":服务端传来的消息:" + content);
                    }

                    for (int i = 0;i <50;i++) {
                        DefiniteLengthParser.sendBytes(sk, (Thread.currentThread().getName() + "测试数字:" + i).getBytes(charset));
                    }

                }

            }
        }


    }

}
