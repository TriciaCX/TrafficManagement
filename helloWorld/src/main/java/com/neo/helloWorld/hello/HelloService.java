package com.neo.helloWorld.hello;

import org.springframework.stereotype.Service;

@Service
public class HelloService {
    public void sayHello(){
        System.out.println("Hello World");
    }
}
