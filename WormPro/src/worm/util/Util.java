package worm.util;

import javax.annotation.processing.AbstractProcessor;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static final String[] UAList = {
      "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36"
            ,"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.11 TaoBrowser/2.0 Safari/536.11"
            ,"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Maxthon/4.4.3.4000 Chrome/30.0.1599.101 Safari/537.36"
            ,"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 UBrowser/4.0.3214.0 Safari/537.36"
            ,"Mozilla/5.0 (iPod; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5"
            ,"MQQBrowser/26 Mozilla/5.0 (Linux; U; Android 2.3.7; zh-cn; MB200 Build/GRJ22; CyanogenMod-7) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1"
            ,"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36 OPR/26.0.1656.60 "
    };

    public static String[] findStringsBetweenR1AndR2(String source,String R1,String R2)
    {
        if(source == null)return null;
        Matcher matcher = Pattern.compile(R1+"(.*?)"+R2).matcher(source);
        List<String> list = new ArrayList<>(100);
        while (matcher.find())
            list.add(matcher.group(1));
        if(list.size() > 0)
            return list.toArray(new String[0]);
        return null;
    }

    public static String getHTMLByRequestedAgent(URL url,String UA) throws Exception {
        String protocol = url.getProtocol();
        if(!protocol.equals("https"))
            throw new Exception("Only support https protocol!");


        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("user-agent",UA);
        connection.setRequestProperty("accept-encoding","utf-8");

        try {
            connection.connect();
            StringBuilder builder = new StringBuilder("");
            String s = null;
            if(connection.getResponseCode() == 200) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((s = reader.readLine()) != null)
                    builder.append(s);
            }
            return builder.toString();
        }
        catch (IOException e){}

        return null;
    }



}
