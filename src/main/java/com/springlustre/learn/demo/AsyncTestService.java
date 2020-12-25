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
