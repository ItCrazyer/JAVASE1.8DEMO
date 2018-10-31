package filedownload;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.SynchronousQueue;

public class FileDownLoadTask extends RecursiveTask<Boolean> {
    long begin;
    long end;
    String remotefile;
    File file;
    InetSocketAddress inetSocketAddress;
    //用阻塞队列和主线程发消息，告诉主线程下载进度
    BlockingQueue<Long> queue;


    public FileDownLoadTask(long begin, long end, String remotefile, File file, InetSocketAddress inetSocketAddress, BlockingQueue<Long> queue) {
        this.begin = begin;
        this.end = end;
        this.remotefile = remotefile;
        this.file = file;
        this.inetSocketAddress = inetSocketAddress;
        this.queue = queue;
    }

    @Override
    protected Boolean compute() {
        boolean flag = false;

        if(end - begin < 2000)
        {

            try (                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            ) {
                randomAccessFile.seek(begin);

                Socket socket = new Socket();
                socket.connect(inetSocketAddress);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                InputStream inputStream = socket.getInputStream();
                writer.write("file " + remotefile + " " + begin + " " + end);
                writer.newLine();
                writer.flush();
                byte[] temp = new byte[50];

                int num;
                long sum = end - begin;
                while ((num = inputStream.read(temp)) > 0) {
                    if (sum > num) {
                        randomAccessFile.write(temp);
                        sum -= num;
                    } else {
                        randomAccessFile.write(temp, 0, (int) sum);
                        break;
                    }
                }
                flag = true;
                queue.put(end - begin);
            }
            catch (Exception e){e.printStackTrace();}
        }
        else
        {
            long mid = (begin+end)/2;
            FileDownLoadTask task2 = new FileDownLoadTask(begin,mid,remotefile,file,inetSocketAddress,queue);
            FileDownLoadTask task3 = new FileDownLoadTask(mid,end,remotefile,file,inetSocketAddress,queue);
            task2.fork();
            task3.fork();
            flag = task2.join()&&task3.join();
        }




        return flag;
    }
}
