package com.neo.helloWorld.service;

import com.neo.helloWorld.hello.HelloService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HelloTest {

    @Resource
    HelloService helloService;

    @Test
    public void sayHelloTest(){
        helloService.sayHello();

    }}
