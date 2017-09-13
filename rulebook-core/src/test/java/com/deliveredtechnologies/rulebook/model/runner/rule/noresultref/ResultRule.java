package com.deliveredtechnologies.rulebook.model.runner.rule.noresultref;

import com.deliveredtechnologies.rulebook.annotation.*;

/**
 * A rule that does not use a result.
 */
@Rule
public class ResultRule {

  @Given("aFact")
  String aFact;

  @Then
  public void then() {
    aFact = aFact + " something";
  }
}

