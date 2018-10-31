package filedownload;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.*;

public class Client {
    private static String remote = "test.rar";
    private static String downloadfile = "D:/my.rar";


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        InetSocketAddress inetSocketAddress = new InetSocketAddress(3000);

        openClient(inetSocketAddress);



    }

    private static void openClient(InetSocketAddress inetSocketAddress) throws IOException, ExecutionException, InterruptedException {

        long len = 0;
        Socket socket = new Socket();
        socket.connect(inetSocketAddress);
        try(
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        )
        {
            writer.write("filelen "+remote);
            writer.newLine();
            writer.flush();
            String s =reader.readLine();
            len = Long.parseLong(s);
            RandomAccessFile file = new RandomAccessFile(downloadfile,"rw");
            file.setLength(len);
        }
        catch (Exception e){}


        //使用阻塞队列进行消息传递，传递下载进度。
        BlockingQueue<Long>queue = new LinkedBlockingDeque<>();
        ForkJoinPool pool = new ForkJoinPool();
        FileDownLoadTask task = new FileDownLoadTask(0,len,remote,new File(downloadfile),inetSocketAddress,queue);
        pool.execute(task);
        final long sum = len;





                long num = 0;
                while (num < sum)
                {
                    try {
                        num += queue.take();

                        System.out.println("已经下载:"+num+"/"+sum);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }






        boolean flag = task.get();
        System.out.println(flag?"下载成功!":"下载失败!");




    }
}
