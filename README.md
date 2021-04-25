### http2dubbo
将http接口转换成dubbo调用（即：使用dubbo协议方式调用spring的web controller！！！）    

不需要更改spring controller代码，直接通过映射和反射将controller当做dubbo service来调用！    

适用于作为统一网关层，将http请求路由到中后台的dubbo service微服务。

#### 用法

先配置Bean：

```java
@Bean
public SpringControllerInvoke controllerInvoker(ApplicationContext applicationContext) {
    return new SpringControllerInvoke(applicationContext);
}
```

然后使用，示例如下：

```java
@Service
public class Http2DubboServiceImpl implements Http2DubboService {
    @Autowired
    private SpringControllerInvoke invoker;
    
    @Override
    public Result<String> route(Request<HttpRequestDto> request) {
        Object result;
        HttpRequestDto dto = request.getData();
        try {
            result = invoker.doInvoke(dto);
        } catch (Throwable e) {
            logger.error("method invoke error, url=" + dto.getRequestURI() + ",method=" + dto.getMethod(), e);
            result = Result.fail(Msg.UNKNOWN_ERR, UT.Excp.getExceptionProfile(e));
        }
        return Result.create(UT.Json.toJSONString(result));
    }
}
```
