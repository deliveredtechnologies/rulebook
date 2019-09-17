package com.deliveredtechnologies.rulebook.runner.test.rulebooks;

import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.Given;
import com.deliveredtechnologies.rulebook.annotation.When;
import java.util.List;
import java.util.Set;

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
