package resolver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.zip.DataFormatException;

public class DefiniteLengthParser {

    //包最长只能400个字节，不够的长度会填充,具体是什么填充不知道
    //用这个方法byteBuffer.clear();实现，把limit调整到capatity的大小
    //
    //规定前四个字节存储长度，即用int存储数据长度
    //则数据最长只能有396个字节
    public static final int lengthOfData = 400;

    public static void main(String[] args) {

    }


    //获取信息采用的方法是一口气读完然后解析长度和数据字段
    //但是会一口气保留所有读取的数据在内存，所以客户端不能频发发送，否则会给服务端内存
    //造成重大影响，在TempData类里规定了服务端存储的最大内存是DefiniteLengthParser.lengthOfData*10个字节
    //也就是说服务端一次性最多存储10个数据包。换句话说能处理最多10个tcp粘包。
    public static void getBytes(SelectionKey sk) throws Exception {
        SocketChannel channel = (SocketChannel)sk.channel();
        TempData tempData = (TempData) sk.attachment();
        if(tempData == null)
            sk.attach(tempData = new TempData(20));

        ByteBuffer byteBuffer = tempData.byteBuffer;
        //一口气读完
        while (channel.read(byteBuffer)>0)
        {
        }

        byteBuffer.flip();
        byte[] bytes = new byte[lengthOfData - 4];
        while (byteBuffer.remaining() >= lengthOfData)
        {
            //读取一个完成的数据包
            int len = byteBuffer.getInt();
            byteBuffer.get(bytes);
            //把数据字段拿出来
            byte[] tempbytes = new byte[len];
            for(int i = 0;i < len;i++)
                tempbytes[i] = bytes[i];
            //得到数据，暂存到队列里
            tempData.queue.add(tempbytes);
        }

        //把可能的半包移动到前面，待下一次处理
        for (int i = 0;i < byteBuffer.remaining();i++)
            byteBuffer.put(i  ,  byteBuffer.get(byteBuffer.position()+i) );
        byteBuffer.position(byteBuffer.remaining());
        byteBuffer.limit(byteBuffer.capacity());

    }

    public static void sendBytes(SelectionKey sk,byte[] bytes) throws Exception {

        int len = bytes.length;

        if(len == 0 || len > lengthOfData-4) {
            System.out.println("发送的数据长度超过396");
            return;
        }

        SocketChannel channel = (SocketChannel)sk.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(lengthOfData);

        byteBuffer.putInt(len);
        byteBuffer.put(bytes);

        byteBuffer.clear();
        channel.write(byteBuffer);
    }



}
