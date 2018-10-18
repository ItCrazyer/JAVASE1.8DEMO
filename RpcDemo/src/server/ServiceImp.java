package server;

import service.Service;

public class ServiceImp implements Service {
    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int LCS(String s1, String s2) {

        int[][] dp = new int[s1.length()][s2.length()];
        dp[0][0] = s1.charAt(0) == s2.charAt(0)?1:0;
        dp[0][1] = s1.charAt(0) == s2.charAt(0)||s1.charAt(0) == s2.charAt(1)?1:0;
        dp[1][0] = s2.charAt(0) == s1.charAt(0)||s2.charAt(0) == s1.charAt(1)?1:0;

        for(int i = 1; i < s1.length();i++)
            for(int j = 1;j < s2.length();j++)
                if(s1.charAt(i) == s2.charAt(j))
                    dp[i][j] = dp[i-1][j-1]+1;
        else
            dp[i][j] = Math.max(dp[i][j-1],dp[i-1][j]);

        return dp[s1.length()-1][s2.length()-1];

    }


}
