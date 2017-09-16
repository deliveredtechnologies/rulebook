package com.deliveredtechnologies.rulebook.model.runner.rule.noresultref;

import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.Given;
import com.deliveredtechnologies.rulebook.annotation.Then;

/**
 * A rule that does not use a result.
 */
@Rule
public class ResultRule {

  @Given("aFact")
  private String _afact;

  @Then
  public void then() {
    _afact = _afact + " something";
  }
}

