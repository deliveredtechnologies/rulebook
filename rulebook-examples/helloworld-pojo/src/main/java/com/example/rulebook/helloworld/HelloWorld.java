package com.example.rulebook.helloworld;

import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;

@Rule
public class HelloWorld {

  @Given("hello")
  private String hello;

  @Given("world")
  private String world;

  @Result
  private String helloworld;

  @When
  public boolean when() {
    return true;
  }

  @Then
  public RuleState then() {
    helloworld = hello + " " + world;
    return RuleState.BREAK;
  }
}
