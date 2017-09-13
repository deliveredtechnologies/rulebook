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
public class NoResultRule {
  @Given("hello")
  private String _hello;

  @Given("world")
  private String _world;

  @Result
  private String _helloworld = null;

  @When
  public boolean when() {
    return _hello != null && _world != null;
  }

  @Then
  public RuleState then() {
    _helloworld = String.format("%s %s", _hello, _world);
    return com.deliveredtechnologies.rulebook.RuleState.BREAK;
  }
}
