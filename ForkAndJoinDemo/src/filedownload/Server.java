package filedownload;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {



    public static void main(String[] args) throws IOException {


        openServer(new InetSocketAddress(3000));
    }

    private static void openServer(InetSocketAddress inetSocketAddress) throws IOException {
        ExecutorService threadpool = Executors.newFixedThreadPool(20);

        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(inetSocketAddress);

        while (true)
        {
            Socket socket = serverSocket.accept();
            threadpool.submit(new Runnable() {
                @Override
                public void run() {
                    try(
                            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            OutputStream outputStream = socket.getOutputStream();
                    )
                    {
                        String s = reader.readLine();
                        if (s!=null)
                        {
                            String[] ss = s.split(" ");
                            RandomAccessFile file = new RandomAccessFile("src/"+ss[1],"rw");
                            if(ss[0].equals("filelen"))
                            {
                                long len = file.length();
                                outputStream.write((len+"").getBytes());
                            }
                            else if(ss[0].equals("file"))
                            {
                                long begin = Long.parseLong(ss[2]);
                                long end = Long.parseLong(ss[3]);
                                file.seek(begin);

                                long sum = end - begin;
                                byte[]temp = new byte[50];
                                int num;
                                while ( (num = file.read(temp))>0 )
                                {
                                    if(sum >=num)
                                    {
                                     outputStream.write(temp);
                                     sum -= num;
                                    }
                                    else if(sum < num)
                                    {
                                        outputStream.write(temp,0,(int)sum);
                                        break;
                                    }
                                }


                            }

                        }

                    }
                    catch (Exception e){e.printStackTrace();}










                }
            });

        }
    }
}
