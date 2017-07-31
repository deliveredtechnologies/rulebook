package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.model.RuleBook;

/**
 * Created by clayton.long on 7/31/17.
 */
public class RuleBookWithResultTypeBuilder<T> {

  private RuleBook<T> _ruleBook;

  public RuleBookWithResultTypeBuilder(RuleBook<T> ruleBook) {
    _ruleBook = ruleBook;
  }

  /**
   * Specifies the default Result value.
   * Note: RuleBooks that return a single Result must have a default Result value set.
   * @param result  the default value of the Result
   * @return        a builder with the default Result value
   */
  public RuleBookDefaultResultBuilder<T> withDefaultResult(T result) {
    _ruleBook.setDefaultResult(result);
    return new RuleBookDefaultResultBuilder<>(_ruleBook);
  }
}
