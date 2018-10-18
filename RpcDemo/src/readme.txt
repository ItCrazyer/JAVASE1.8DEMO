虽然服务端和客户端在一个项目里，但是可以分别拿出然后变成两个项目。
具体做法是client包+service包是一个项目。
server包+service包是另外一个两目。
两者都使用了service包里的公共接口和类。

ide使用的是idea，java环境为java8