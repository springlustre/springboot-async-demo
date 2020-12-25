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
