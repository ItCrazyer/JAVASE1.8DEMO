package wordcount;

import java.io.*;
import java.math.BigInteger;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/***
 * 使用ForkAndJoin框架统计单词数目
 * filepath为指定的路径,可以在启动时赋值，也可以直接在程序里给定
 * charset为文件的编码
 */
public class WordCountTest {

    private static Charset charset = Charset.forName("gbk");
    private static String filepath = "D:/codeblocks_fiels";
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        if(args.length > 1){filepath = args[0];charset = Charset.forName(args[1]);}

        File file = new File(filepath);
        if(!file.exists()){
            System.out.println("文件夹不存在!!!");
            return;
        }
        if(!file.isDirectory())
        {
            System.out.println("不是文件夹!!!");
            return;
        }
        File[] temp = file.listFiles();
        ArrayList<File> arr = new ArrayList<>();
        for(File f:temp)
            if(f.isFile())arr.add(f);

        File[]files = arr.toArray(new File[arr.size()]);
        if(files.length == 0)
        {
            System.out.println("文件夹下文件数目为0!!!");
            return;
        }
        System.out.println("共有"+files.length+"个文件需要计算!!!");



        //TransferQueue比SynchronousQueue更强大，SynchronousQueue的生产者必须等待
        //数据被消费者消费，否则阻塞，它的put方法和TransferQueue.transfer一样
        //兼具传统的ArrayBlockingQueue和SynchronousQueue的特性

        BlockingQueue<Integer> queue = new LinkedBlockingDeque<>();



        //下面开始计算
        ForkJoinPool pool = new ForkJoinPool();


        //execute方法没有返回值，需要先声明WordCountTask
        //而如果用submit方法会返回一个future对象,即可以用ForkJoinTask.join()也而可以用future.get()获得返回值
        //WordCountTask task = new WordCountTask(files,0,files.length,queue);
        //pool.execute(task);

        Future<BigInteger> task = pool.submit(new WordCountTask(files,0,files.length,queue));
        //统计执行好的文件


                int sum = files.length;
                int num = 0;
                while (num < sum)
                    try
                {
                    num += queue.take();
                    System.out.println("Number of work that has been done:"+num+"/"+sum);
                }
                catch (Exception e){e.printStackTrace();}


        BigInteger sum2 = (BigInteger) task.get();



        System.out.println(Thread.currentThread().getName()+"单词总数目:"+sum2+"个");


    }






    private static class WordCountTask extends RecursiveTask<BigInteger>
    {

        File[] files;
        int begin;
        int end;
        BlockingQueue<Integer> queue;



        public WordCountTask(File[] files, int begin, int end, BlockingQueue<Integer> queue) {
            this.files = files;
            this.begin = begin;
            this.end = end;
            this.queue = queue;
        }





        @Override
        protected BigInteger compute() {
            BigInteger sum = new BigInteger("0");
            if(end - begin < 5)
            {
                for(int i = begin;i < end;i++)
                {
                    File file = files[i];

                    try (
                        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))
                    )
                    {
                        String s;
                        int tempsum = 0;
                        while ((s = reader.readLine())!=null)
                        {
                            tempsum += s.split(" ").length;
                        }
                        sum = sum.add(new BigInteger(tempsum+""));

                        queue.put(end - begin);

                    } catch (Exception e) { e.printStackTrace(); }


                }


            }
            else {
                int mid = (begin + end)/2;
                WordCountTask task2 = new WordCountTask(files,begin,mid,queue);
                WordCountTask task3 = new WordCountTask(files,mid,end,queue);
                task2.fork();
                task3.fork();
                BigInteger t2 = task2.join();
                BigInteger t3 = task3.join();
                sum = t2.add(t3);
            }


            return sum;
        }




    }
}




