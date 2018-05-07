package com.deliveredtechnologies.rulebook.spring;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.annotation.Given;
import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.Then;
import com.deliveredtechnologies.rulebook.annotation.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Rule(order = 3)
public class SpringAwareRuleWithoutResult {
  @Autowired
  private SpringTestService _springTestService;

  @Given("value2")
  private Fact<String> _value2;

  @When
  public boolean when() {
    return _springTestService != null;
  }

  @Then
  public void then() {
    _value2.setValue(_springTestService.getValue());
  }
}