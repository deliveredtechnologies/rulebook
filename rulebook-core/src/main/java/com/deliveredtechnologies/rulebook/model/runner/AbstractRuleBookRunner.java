package com.deliveredtechnologies.rulebook.model.runner;

import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.Auditor;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.AuditableRule;
import com.deliveredtechnologies.rulebook.model.Auditable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.List;
import java.util.Optional;

import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotatedField;
import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotation;

/**
 * Declares an abstract class for creating RuleBookRunner derived classes using the template method pattern.
 */
public abstract class AbstractRuleBookRunner extends Auditor implements RuleBook {
  private static Logger LOGGER = LoggerFactory.getLogger(RuleBookRunner.class);

  private Class<? extends RuleBook> _prototypeClass;

  @SuppressWarnings("unchecked")
  private Result _result = new Result(null);

  /**
   * Creates a new RuleBookRunner using the RuleBook class.
   * @param prototypeClass a RuleBook class that determines how the RuleBook is run
   */
  public AbstractRuleBookRunner(Class<? extends RuleBook> prototypeClass) {
    _prototypeClass = prototypeClass;
  }

  @Override
  public void addRule(Rule rule) {
    throw new UnsupportedOperationException("Rules are only added to a RuleBookRunner on run()!");
  }

  @Override
  @SuppressWarnings("unchecked")
  public void run(NameValueReferableMap facts) {
    getResult().ifPresent(Result::reset);
    try {
      RuleBook ruleBook = _prototypeClass.newInstance();
      List<Class<?>> classes = getPojoRules();
      for (Class<?> rule : classes) {
        getAnnotatedField(com.deliveredtechnologies.rulebook.annotation.Result.class, rule).ifPresent(field ->
            ruleBook.setDefaultResult(_result.getValue())
        );
        String name = getAnnotation(com.deliveredtechnologies.rulebook.annotation.Rule.class, rule).name();
        if (name.equals("None")) {
          name = rule.getSimpleName();
        }
        Object ruleInstance = getRuleInstance(rule);
        if (ruleInstance == null) {
          continue;
        }
        Rule auditableRule = new AuditableRule(new RuleAdapter(ruleInstance), name);
        ruleBook.addRule(auditableRule);
        ((Auditable)auditableRule).setAuditor(this);
      }
      ruleBook.run(facts);
      Optional<Result> result = ruleBook.getResult();
      result.ifPresent(res -> _result.setValue(res.getValue()));
    } catch (IOException | InvalidPathException ex) {
      LOGGER.error("Unable to find rule classes", ex);
    } catch (InstantiationException | IllegalAccessException ex) {
      LOGGER.error("Unable to create an instance of '" + _prototypeClass.getName()
          + "' with the default constructor", ex);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void setDefaultResult(Object result) {
    _result = new Result(result);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<Result> getResult() {
    return _result.getValue() == null ? Optional.empty() : Optional.of(_result);
  }

  @Override
  public boolean hasRules() {
    try {
      return getPojoRules().size() > 0;
    } catch (InvalidPathException e) {
      LOGGER.error("Unable to find rule classes", e);
      return false;
    }
  }

  /**
   * Returns a rule instance
   *
   * <p>For container aware contexts (like Spring, Guice, Weld, etc.) override this method and instantiate the rule
   * via the container.
   * @param rule The rule class
   * @return The rule instance
   */
  protected Object getRuleInstance(Class<?> rule) {
    try {
      return rule.newInstance();
    } catch (InstantiationException | IllegalAccessException ex) {
      LOGGER.warn("Unable to create instance of rule using '" + rule + "'", ex);
    }
    return null;
  }

  /**
   * Gets the POJO Rules used in the RuleBook.
   * @return  A List of the POJO Rules used in a RuleBook.
   */
  protected abstract List<Class<?>> getPojoRules();
}
