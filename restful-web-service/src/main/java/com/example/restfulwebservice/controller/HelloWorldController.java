package com.example.restfulwebservice.controller;

import com.example.restfulwebservice.HelloWorldBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    //GET
    //hello-world (endpoint)
    //@RequestMapping(method=RequestMethod.GET, path="endpoint") == @GetMapping(path="endpoint")
    @GetMapping(path = "/hello-world")
    public String helloWorld(){
        return "Hello World";
    }

    @GetMapping(path = "/hello-world-bean")
    public HelloWorldBean helloWorldBean(){
        return new HelloWorldBean("Hello World");
    }

}
