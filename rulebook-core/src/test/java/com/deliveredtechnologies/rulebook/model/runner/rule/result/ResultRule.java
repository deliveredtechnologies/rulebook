package com.deliveredtechnologies.rulebook.model.runner.rule.result;

import com.deliveredtechnologies.rulebook.annotation.Result;
import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.Then;

/**
 * A rule that uses a result.
 */
@Rule
public class ResultRule {
  @Result
  private double _result;

  @Then
  public void then() {
    _result += 1.5;
  }
}

