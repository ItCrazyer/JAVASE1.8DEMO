package service;

import java.io.Serializable;

//RPC的传输协议
//这里使用java自带的序列化进行对象序列化
//所以实现可序列化接口
//这个service包下的两个类应该是客户端和服务端都拥有的
public class Protocol implements Serializable {

    //协议包括
    //函数名字
    //函数参数的类类型
    //函数参数
    //返回结果
    //其中函数名+函数参数的类类型使用反射技术可以准确定位函数
    //而函数参数则是为了调用函数时赋值
    public String funname;
    public Class[] parametertype;
    public Object[] args;

    //返回的结果
    public Object result;


    public Protocol(String funname, Class[] parametertype, Object[] args, Object result) {
        this.funname = funname;
        this.parametertype = parametertype;
        this.args = args;
        this.result = result;
    }

    public Protocol(){}


}
