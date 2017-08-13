package com.example.rulebook.helloworld;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {
  public static void main(String[] args) {

    ConfigurableApplicationContext context = new SpringApplicationBuilder()
        .sources(Application.class)
        .bannerMode(Banner.Mode.OFF)
        .run(args);

    HelloWorldOutputBean helloWorld = context.getBean(HelloWorldOutputBean.class);
    helloWorld.printResult();
  }
}
