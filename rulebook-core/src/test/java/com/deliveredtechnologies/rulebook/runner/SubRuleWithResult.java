package com.deliveredtechnologies.rulebook.runner;

import com.deliveredtechnologies.rulebook.annotation.Given;
import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.When;

/**
 * Created by clong on 3/12/17.
 */
@SubRuleAnnotation
public class SubRuleWithResult extends SampleRuleWithResult {
  @Given("fact1")
  String _subFact;

  @When
  public boolean condition() {
    return getFact1().equals(getFact2());
  }

  public String getSubFact() {
    return _subFact;
  }
}
