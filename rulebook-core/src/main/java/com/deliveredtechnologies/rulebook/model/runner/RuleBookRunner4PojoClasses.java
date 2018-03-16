package com.deliveredtechnologies.rulebook.model.runner;

import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Runs a List of POJO Rules as a RuleBook.
 */
public class RuleBookRunner4PojoClasses extends AbstractRuleBookRunner {

  private static Logger LOGGER = LoggerFactory.getLogger(RuleBookRunner.class);

  private List<Class<?>> _pojoRules;
  private Class<? extends RuleBook> _prototypeClass;

  @SuppressWarnings("unchecked")
  private Result _result = new Result(null);

  /**
   * Creates a new RuleBookRunner using the specified POJO Rules and the default RuleBook.
   * @param pojoRules the POJO Rule classes used in the RuleBook
   */
  public RuleBookRunner4PojoClasses(List<Class<?>> pojoRules) {
    this(CoRRuleBook.class, pojoRules);
  }

  /**
   * Creates a new RuleBookRunner using the specified POJO Rules and the supplied RuleBook.
   * @param ruleBookClass the RuleBook type to use as a delegate for the RuleBookRunner
   * @param pojoRules     the package to scan for POJO rules
   */
  public RuleBookRunner4PojoClasses(Class<? extends RuleBook> ruleBookClass, List<Class<?>> pojoRules) {
    super(ruleBookClass);
    _prototypeClass = ruleBookClass;
    _pojoRules = pojoRules;
  }

  /**
   * Gets the POJO rules to be used in the RuleBook.
   * @return  The List of the supplied POJO Rules
   */
  protected List<Class<?>> getPojoRules() {
    return _pojoRules;
  }
}
