package com.deliveredtechnologies.rulebook.model.runner;

import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.*;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotatedField;
import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotation;
import static java.util.Comparator.comparingInt;

/**
 * Runs the POJO Rules in a specified package as a RuleBook.
 */
public class RuleBookRunner extends Auditor implements RuleBook {

  private static Logger LOGGER = LoggerFactory.getLogger(RuleBookRunner.class);

  private String _package;
  private Class<? extends RuleBook> _prototypeClass;

  @SuppressWarnings("unchecked")
  private Result _result = new Result(null);

  /**
   * Creates a new RuleBookRunner using the specified package and the default RuleBook.
   * @param rulePackage a package to scan for POJO Rules
   */
  public RuleBookRunner(String rulePackage) {
    this(CoRRuleBook.class, rulePackage);
  }

  /**
   * Creates a new RuleBookRunner using the specified package and the supplied RuleBook.
   * @param ruleBookClass the RuleBook type to use as a delegate for the RuleBookRunner
   * @param rulePackage   the package to scan for POJO rules
   */
  public RuleBookRunner(Class<? extends RuleBook> ruleBookClass, String rulePackage) {
    _prototypeClass = ruleBookClass;
    _package = rulePackage;
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
      List<Class<?>> classes = findRuleClassesInPackage(_package);
      for (Class<?> rule : classes) {
        try {
          getAnnotatedField(com.deliveredtechnologies.rulebook.annotation.Result.class, rule).ifPresent(field ->
              ruleBook.setDefaultResult(_result.getValue() == null ? new Object() : _result.getValue())
          );
          String name = getAnnotation(com.deliveredtechnologies.rulebook.annotation.Rule.class, rule).name();
          if (name.equals("None")) {
            name = rule.getSimpleName();
          }
          Rule ruleInstance = new AuditableRule(new RuleAdapter(rule.newInstance()), name);
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
      LOGGER.error("Unable to find rule classes in package '" + _package + "'", ex);
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
      return findRuleClassesInPackage(_package).size() > 0;
    } catch (IOException | InvalidPathException e) {
      LOGGER.error("Unable to find rule classes in package '" + _package + "'", e);
      return false;
    }
  }

  private List<Class<?>> findRuleClassesInPackage(String packageName) throws InvalidPathException, IOException {
    Reflections reflections = new Reflections(packageName);

    List<Class<?>> rules = reflections
        .getTypesAnnotatedWith(com.deliveredtechnologies.rulebook.annotation.Rule.class).stream()
        .filter(rule -> packageName.equals(rule.getPackage().getName())) // Search only within package, not subpackages
        .filter(rule -> rule.getAnnotatedSuperclass() != null) // Include classes only, exclude interfaces, etc.
        .collect(Collectors.toList());

    rules.sort(comparingInt(aClass ->
        getAnnotation(com.deliveredtechnologies.rulebook.annotation.Rule.class, aClass).order()));

    return rules;
  }

}
