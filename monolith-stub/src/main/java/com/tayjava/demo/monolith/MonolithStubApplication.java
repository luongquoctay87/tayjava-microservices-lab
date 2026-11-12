package com.tayjava.demo.monolith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Stand-in for the TayJava modular monolith during a Strangler Fig cutover. */
@SpringBootApplication
public class MonolithStubApplication {

  public static void main(String[] args) {
    SpringApplication.run(MonolithStubApplication.class, args);
  }
}
