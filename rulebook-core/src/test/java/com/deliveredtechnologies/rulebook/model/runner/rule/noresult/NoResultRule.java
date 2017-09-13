package com.deliveredtechnologies.rulebook.model.runner.rule.noresult;

import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.Given;
import com.deliveredtechnologies.rulebook.annotation.Result;
import com.deliveredtechnologies.rulebook.annotation.When;
import com.deliveredtechnologies.rulebook.annotation.Then;

/**
 * A rule whose result is null.
 */
@Rule
public class NoResultRule
{
  @Given("hello")
  private String	hello;

  @Given("world")
  private String	world;

  @Result
  private String	helloworld = null;

  @When
  public boolean when()
  {
    return hello != null && world != null;
  }
  @Then
  public RuleState then()
  {
    helloworld = String.format("%s %s",hello, world);
    return com.deliveredtechnologies.rulebook.RuleState.BREAK;
  }
}
