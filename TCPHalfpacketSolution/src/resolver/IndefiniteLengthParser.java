package resolver;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.zip.DataFormatException;


//不定长TCP 粘包/半包处理器
//原理和定长的差不多，但是对于java nio来讲，应该是高并发，小数据量。
//不定长意味着可以传送大量的数据，虽然可以实现，但是我觉得这已经不适合
//nio了，具体实现做法把TempData类中再增加一个字段，用来表示未读完的包的
//剩余容量，还是一次性读取数据到内存，然后先看新增字段值是否为0，不为0表示
//上一轮有未读完的，为0表示重新读，那么重新读取长度
// 新的TempData可以参考下面的内部类
//不定长的客户端和服务端只要把DefiniteLengthParser的地方换成IndefiniteLengthParser就可以了
//不需要担心TempData tempData = (TempData) sk.attachment()，因为下面的TempData类是resolve.TempData
//的子类，所以只换DefiniteLengthParser就可以了！！！

@Deprecated
public class IndefiniteLengthParser {


    //虽然是不定长，但是还是需要设置最大长度
    //不能多长都给传，否则会对服务器造成不可估计的后果
    public static final int max_size = 600;

    public static void getBytes(SelectionKey sk) throws Exception {
        SocketChannel channel = (SocketChannel)sk.channel();
        TempData tempData = (TempData)sk.attachment();
        if(tempData == null)
        sk.attach(tempData = new TempData(20));

        ByteBuffer byteBuffer = tempData.byteBuffer;
        //一直读完
        while (channel.read(byteBuffer) > 0){}

        byteBuffer.flip();
        //下面进行处理
        while (true)
        {
            //还需要left个字节才能拼成完整的包，而剩余的不够了，则退出
            //或者是下一轮开始，长度还未读出，但是不够4个字节，无法读取长度
            if(tempData.left > byteBuffer.remaining() || tempData.left == 0&&byteBuffer.remaining() < 4) {
            for(int i = 0;i < byteBuffer.remaining();i++)
                byteBuffer.put(i,byteBuffer.get(byteBuffer.position()+i));
            byteBuffer.position(byteBuffer.remaining());
            byteBuffer.limit(byteBuffer.capacity());
                break;
            }
            int left;
            if(tempData.left == 0)
                left = byteBuffer.getInt();
            else left = tempData.left;

            if (left <= byteBuffer.remaining())
            {
                byte[] bytes = new byte[left];
                byteBuffer.get(bytes);
               tempData.queue.add(bytes);
               left = 0;
            }

            tempData.left = left;

        }




    }



    public static void sendBytes(SelectionKey sk,byte[] bytes) throws Exception {

        int len = bytes.length;
        if(len > max_size - 4){
            System.out.println("超过最大长度，不可发送!");return;}

            SocketChannel channel = (SocketChannel)sk.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(len+4);
        byteBuffer.putInt(len);
        byteBuffer.put(bytes);
        byteBuffer.flip();
        channel.write(byteBuffer);

    }

    private static class TempData extends resolver.TempData
    {
        int left = 0;
        public TempData(int size) throws DataFormatException {
            super(size);
            //缓存长度设置为最大长度，这个设置不能小于max_size，也就是单包的长度
            //设置5*max_size意味着最多允许5个最大长度的包粘包
            byteBuffer = ByteBuffer.allocate(max_size*5);
        }
    }

}
