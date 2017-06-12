package com.deliveredtechnologies.rulebook.model.runner;

import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.RuleBookFactory;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;

/**
 * A an implementation of a RuleBook factory for RuleBookRunners using the factory method pattern.
 */
public class RuleBookRunnerFactory implements RuleBookFactory {

  private Class<? extends RuleBook> _ruleBookType;
  private String _pkg;

  /**
   * Creates a RuleBookRunnerFactory using the type of RuleBook to be used in the RuleBookRunner and the package.
   * @param ruleBookType  the type of RuleBook to be used in the RuleBookRunner
   * @param pkg           the Java package name
   */
  public RuleBookRunnerFactory(Class<? extends RuleBook> ruleBookType, String pkg) {
    _ruleBookType = ruleBookType;
    _pkg = pkg;
  }

  /**
   * Creates a RuleBookRunnerFactory using the package name only and a default RuleBook implementation.
   * @param pkg           the Java package name
   */
  public RuleBookRunnerFactory(String pkg) {
    this(CoRRuleBook.class, pkg);
  }

  /**
   * Creates a new RuleBookRunner.
   * @return  a new RuleBookRunner
   */
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
