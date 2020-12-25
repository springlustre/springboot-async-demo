SpringBoot 中使用@Async实现异步调用



本文代码示例见github：https://github.com/springlustre/springboot-async-demo

一、为什么要用异步

在java应用中，大多是通过同步的方式来实现交互处理的；但是容易造成响应迟缓的情况，此时我们可以使用异步的方式来缩短响应时间。

二、如何使用@Async注解

1、在springboot启动类当中添加注解`@EnableAsync`注解。

```java
package com.springlustre.learn.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DemoApplication {
   public static void main(String[] args) {
      SpringApplication.run(DemoApplication.class, args);
   }
}
```



2、编写Service类，在方法前添加@Async注解，可以分为有返回值和无返回值的两种情况

```java
package com.springlustre.learn.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.Future;

@Component
@Slf4j
public class AsyncTestService {
    @Async
    public Future<String> futureTask1() throws Exception {
        log.info("futureTask1开始执行");
        long start = System.currentTimeMillis();
        Thread.sleep((new Random()).nextInt(1000*10));
        long end = System.currentTimeMillis();
        log.info("futureTask1执行结束，耗时：{}毫秒",(end - start));
        return new AsyncResult<>("futureTask1");
    }

    @Async
    public void voidTask1() throws Exception {
        log.info("voidTask1开始执行");
        long start = System.currentTimeMillis();
        Thread.sleep((new Random()).nextInt(1000*10));
        long end = System.currentTimeMillis();
        log.info("voidTask1执行结束，耗时：{}毫秒",(end - start));
    }

}

```



3、测试

编写测试用例

```java
package com.springlustre.learn.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.Future;

@SpringBootTest
class AsyncTestServerTests {

   @Autowired
   AsyncTestService asyncTestService;

   @Test
   void test1() throws Exception{
      System.out.println("测试开始");
      asyncTestService.voidTask1();
      System.out.println("测试结束");
      Thread.sleep(10*1000);
   }

   @Test
   void test2() throws Exception{
      System.out.println("测试开始");
      Future<String> one = asyncTestService.futureTask1();
      System.out.println("测试结束");
      while (true) {
         if (one.isDone()) {
            System.out.println(one.get());
            break;
         }
      }
      Thread.sleep(10*1000);
   }

}
```



test1的测试结果：

```java
测试开始
测试结束
2020-12-25 16:06:13.354  INFO 15444 --- [         task-1] c.s.learn.demo.AsyncTestService          : voidTask1开始执行
2020-12-25 16:06:17.237  INFO 15444 --- [         task-1] c.s.learn.demo.AsyncTestService          : voidTask1执行结束，耗时：3882毫秒

2020-12-25 16:06:23.426  INFO 15444 --- [extShutdownHook] o.s.s.concurrent.ThreadPoolTaskExecutor  : Shutting down ExecutorService 'applicationTaskExecutor'
```



test2的测试结果：

```
测试开始
测试结束
2020-12-25 16:33:43.246  INFO 13524 --- [         task-1] c.s.learn.demo.AsyncTestService          : futureTask1开始执行
2020-12-25 16:33:47.671  INFO 13524 --- [         task-1] c.s.learn.demo.AsyncTestService          : futureTask1执行结束，耗时：4425毫秒
futureTask1
```



三、线程池配置

```java
package com.springlustre.learn.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {

    @Bean
    public TaskExecutor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(3);
        executor.setKeepAliveSeconds(60);
        executor.setQueueCapacity(2);
        executor.setThreadNamePrefix("my-async-thread");
        return executor;
    }
}
```

再次测试，发现线程是我们自定义的了

```
测试开始
测试结束
2020-12-25 16:49:06.331  INFO 9580 --- [y-async-thread1] c.s.learn.demo.AsyncTestService          : futureTask1开始执行
2020-12-25 16:49:12.038  INFO 9580 --- [y-async-thread1] c.s.learn.demo.AsyncTestService          : futureTask1执行结束，耗时：5707毫秒
futureTask1
```



四、注意事项

1、异步方法使用注解@Async的返回值只能为void或者Future

2、注解的方法必须是`public`方法

3、异步方法和调用方法一般要写在不同的类中，如果是同一个类要使用代理类。

具体方式如下：

（1）注入ApplicationContext

```java
@Autowired
private ApplicationContext applicationContext;
```

（2）通过ApplicationContext对象获取当前类对象

```java
 applicationContext.getBean(Class);
```





