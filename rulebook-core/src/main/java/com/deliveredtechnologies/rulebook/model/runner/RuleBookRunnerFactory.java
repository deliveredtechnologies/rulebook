package com.deliveredtechnologies.rulebook.model.runner;

import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.RuleBookFactory;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;

public class RuleBookRunnerFactory implements RuleBookFactory {

  private Class<? extends RuleBook> _ruleBookType;
  private String _pkg;

  public RuleBookRunnerFactory(Class<? extends RuleBook> ruleBookType, String pkg) {
    _ruleBookType = ruleBookType;
    _pkg = pkg;
  }

  public RuleBookRunnerFactory(String pkg) {
    this(CoRRuleBook.class, pkg);
  }

  @Override
  public RuleBook createRuleBook() {
    try {
      RuleBook ruleBook = _ruleBookType.newInstance();
      return new RuleBookRunner(ruleBook, _pkg);
    } catch (InstantiationException | IllegalAccessException e) {
      return new RuleBookRunner(_pkg);
    }
  }
}
