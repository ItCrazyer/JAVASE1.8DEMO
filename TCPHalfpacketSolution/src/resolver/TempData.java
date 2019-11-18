package resolver;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.zip.DataFormatException;

public class TempData {
    private static final int max_size = 20;

    //消息暂存队列，最大只能存储max_size条消息
    public Queue<byte[]> queue;

    public ByteBuffer byteBuffer;

    public TempData(int size) throws DataFormatException {
        if(max_size <= 0 ||size >max_size)
            throw new DataFormatException("队列长度不正确或超过"+max_size+"，失败！！！");
        queue = new ArrayDeque<>(max_size);

        //最大缓存的所有数据包的长度不能超过4000字节
        byteBuffer = ByteBuffer.allocate(DefiniteLengthParser.lengthOfData*10);


    }

}
