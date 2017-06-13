package com.deliveredtechnologies.rulebook.model.runner;

import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.RuleBookFactory;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A an implementation of a RuleBook factory for RuleBookRunners using the factory method pattern.
 */
public class RuleBookRunnerFactory implements RuleBookFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(RuleBookRunnerFactory.class);

  private Class<? extends RuleBook> _ruleBookType;
  private String _pkg;

  /**
   * Creates a RuleBookRunnerFactory using the type of RuleBook to be used in the RuleBookRunner and the package.
   * @param ruleBookType  the type of RuleBook to be used in the RuleBookRunner
   * @param pkg           the Java package name
   */
  public RuleBookRunnerFactory(Class<? extends RuleBook> ruleBookType, String pkg) {
    LOGGER.debug("Creating RuleBookRunnerFactory with RuleBook type: " + ruleBookType.getName() + "; package: " + pkg);
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
      LOGGER.debug("Creating a RuleBookRunner using " + _ruleBookType.getName() + " and package " + _pkg);
      RuleBook ruleBook = _ruleBookType.newInstance();
      return new RuleBookRunner(ruleBook, _pkg);
    } catch (InstantiationException | IllegalAccessException e) {
      LOGGER.warn("Error creating RuleBookRunner with RuleBook type " + _ruleBookType.getName() +
          "; using default RuleBook type" ,e);
      return new RuleBookRunner(_pkg);
    }
  }
}
