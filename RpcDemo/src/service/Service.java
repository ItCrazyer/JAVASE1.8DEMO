package service;


//service接口
//这个接口定义了服务端提供的服务，同时定义了客户端能调用的功能
//这个service包下的两个类应该是客户端和服务端都拥有的
public interface Service {

    int add(int a,int b);


    //求最长上升子序列的长度
    int LCS(String s1,String s2);
}
