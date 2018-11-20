package com.deliveredtechnologies.rulebook.model.runner.test.rulebooks.error.condition;

import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.Then;
import com.deliveredtechnologies.rulebook.annotation.When;

/**
 * Created by clayton.long on 5/8/18.
 */
@Rule(order = 1)
public class SampleRule {
  @When
  public boolean when() {
    return true;
  }

  @Then
  public void then() {}
}
