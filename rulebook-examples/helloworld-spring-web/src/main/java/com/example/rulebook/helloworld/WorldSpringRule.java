package com.example.rulebook.helloworld;

import com.deliveredtechnologies.rulebook.annotation.*;

@Rule(order = 2)
public class WorldSpringRule {
  @Given("world")
  private String world;

  @Result
  private String result;

  @When
  public boolean when() {
    return world != null;
  }

  @Then
  public void then() {
    result += world;
  }
}
