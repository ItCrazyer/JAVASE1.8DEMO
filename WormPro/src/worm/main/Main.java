package worm.main;

import worm.util.Util;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        ExecutorService threadpool =  Executors.newCachedThreadPool();
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1000);

        System.setErr(new PrintStream(new FileOutputStream("err.txt")));

        threadpool.execute(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted())
                {
                    try {
                        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                        while (!(hour >= 10 && hour <= 24))
                            TimeUnit.SECONDS.sleep(1000);

                        int loc = new Random().nextInt(Util.UAList.length);
                        URL url = new URL("https://segmentfault.com/u/qiuqi_turing/articles");
                        String s = Util.getHTMLByRequestedAgent(url,Util.UAList[loc]);

                        String[] articleList = Util.findStringsBetweenR1AndR2(s,"href=\"/a/","\"");

                        for(String article:articleList)
                            blockingQueue.offer(article);
                        //get the list every 100 seconds
                        TimeUnit.SECONDS.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        for(int i = 0;i < 20;i++)
            threadpool.execute(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.interrupted()) {
                        try {
                            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                            while (!(hour >= 10 && hour <= 24))
                                TimeUnit.SECONDS.sleep(1000);
                            String articleID = null;
                            while ((articleID = blockingQueue.poll()) == null )
                                TimeUnit.SECONDS.sleep(10);
                            URL url = new URL("https://segmentfault.com/a/"+articleID);

                            int loc = new Random().nextInt(Util.UAList.length);
                            Util.getHTMLByRequestedAgent(url,Util.UAList[loc]);
                            //String html = Util.getHTMLByRequestedAgent(url,Util.UAList[loc]);
                            //String[] strings = Util.findStringsBetweenR1AndR2(html,"<title>","</title>");

                            TimeUnit.MINUTES.sleep(3);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    }

}
