package com.example.rulebook.helloworld;

import com.deliveredtechnologies.rulebook.annotation.*;

@Rule(order = 1)
public class HelloSpringRule {
  @Given("hello")
  private String hello;

  @Result
  private String result;

  @When
  public boolean when() {
    return hello != null;
  }

  @Then
  public void then() {
    result = hello + " ";
  }
}
