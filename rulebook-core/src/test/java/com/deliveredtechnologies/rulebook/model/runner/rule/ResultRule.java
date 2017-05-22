package com.deliveredtechnologies.rulebook.model.runner.rule;

import com.deliveredtechnologies.rulebook.annotation.Result;
import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.Then;
import com.deliveredtechnologies.rulebook.annotation.When;

/**
 * Created by clong on 5/22/17.
 */
@Rule
public class ResultRule {
  @Result
  private double _result;

  @When
  public boolean when() {
    return true;
  }

  @Then
  public void then() {
    _result += 1.5;
  }
}

