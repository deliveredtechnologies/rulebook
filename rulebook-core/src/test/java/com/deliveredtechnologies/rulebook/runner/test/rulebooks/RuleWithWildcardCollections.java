package com.deliveredtechnologies.rulebook.runner.test.rulebooks;

import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.Given;
import com.deliveredtechnologies.rulebook.annotation.Result;
import com.deliveredtechnologies.rulebook.annotation.Rule;
import com.deliveredtechnologies.rulebook.annotation.Then;
import java.util.List;
import java.util.Set;



@Rule
public class RuleWithWildcardCollections<T extends Number> {

  @Given
  public Set<? extends CharSequence> _charSequenceSet;

  @Given
  public List<T> _numberSet;

  @Result
  Set<? extends CharSequence> _result;

  @Then
  public RuleState then() {
    _result = _charSequenceSet;
    return RuleState.NEXT;
  }
}
