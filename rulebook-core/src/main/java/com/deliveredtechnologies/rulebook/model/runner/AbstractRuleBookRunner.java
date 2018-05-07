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
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.List;
import java.util.Optional;

import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotatedField;
import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotation;

/**
 * Declares an abstract class for creating RuleBookRunner derived classes using the template method pattern.
 */
public abstract class AbstractRuleBookRunner extends Auditor implements RuleBook, ApplicationContextAware {
  private static Logger LOGGER = LoggerFactory.getLogger(RuleBookRunner.class);

  private Class<? extends RuleBook> _prototypeClass;

  @SuppressWarnings("unchecked")
  private Result _result = new Result(null);

  private ApplicationContext _applicationContext;

  /**
   * Creates a new RuleBookRunner using the RuleBook class.
   * @param prototypeClass a RuleBook class that determines how the RuleBook is run
   */
  public AbstractRuleBookRunner(Class<? extends RuleBook> prototypeClass) {
    _prototypeClass = prototypeClass;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this._applicationContext = applicationContext;
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
        try {
          getAnnotatedField(com.deliveredtechnologies.rulebook.annotation.Result.class, rule).ifPresent(field ->
              ruleBook.setDefaultResult(_result.getValue())
          );
          String name = getAnnotation(com.deliveredtechnologies.rulebook.annotation.Rule.class, rule).name();
          if (name.equals("None")) {
            name = rule.getSimpleName();
          }
          Rule ruleInstance = new AuditableRule(new RuleAdapter(getRuleInstance(rule)), name);
          ruleBook.addRule(ruleInstance);
          ((Auditable)ruleInstance).setAuditor(this);
        } catch (IllegalAccessException | InstantiationException ex) {
          LOGGER.warn("Unable to create instance of rule using '" + rule + "'", ex);
        }
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

  private Object getRuleInstance(Class<?> rule) throws IllegalAccessException, InstantiationException {
    // For backwards compatibility, if not within a Spring project
    if (_applicationContext == null) {
      return rule.newInstance();
    }

    try {
      // Spring bean POJO rule found
      return _applicationContext.getBean(rule);
    } catch (BeansException e) {
      // POJO rule isn't a Spring bean
      return rule.newInstance();
    }
  }

  /**
   * Gets the POJO Rules used in the RuleBook.
   * @return  A List of the POJO Rules used in a RuleBook.
   */
  protected abstract List<Class<?>> getPojoRules();
}
