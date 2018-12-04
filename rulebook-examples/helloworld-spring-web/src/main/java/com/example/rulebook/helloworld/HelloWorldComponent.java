package com.example.rulebook.helloworld;

import org.springframework.stereotype.Component;

@Component
public class HelloWorldComponent {
  public String getHelloWorld(String hello, String world) {
    return hello + " " + world + "!";
  }
}
