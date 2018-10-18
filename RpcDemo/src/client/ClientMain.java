package client;

import service.Service;

import java.io.IOException;

public class ClientMain {

    public static void main(String[] args) throws IOException {

        //使用动态代理，而不是静态代理
        //因为动态代理的的处理接口InvocationHandler中的invoke方法
        //已经通过反射技术把我们需要的东西都拿出来了，并且invoke对
        //被代理的对象的每一个函数都会调用，如果用静态代理的话，首先
        //得自己通过反射获取需要东西，其次还需要自己在代理对象的每一个
        //函数中自己调用，太麻烦，这应该就是动态代理的好处！！！
        Service service = (Service)DynamicProxyFactory.getProxy();

        //计算两者之和
        System.out.println(service.add(5,6));

        //计算最长上升子序列，动态规划基础题
        System.out.println(service.LCS("13dsada","dsaa1f"));




    }
}
