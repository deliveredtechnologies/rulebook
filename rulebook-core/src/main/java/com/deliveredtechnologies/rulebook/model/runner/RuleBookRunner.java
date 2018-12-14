package com.deliveredtechnologies.rulebook.model.runner;

import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotation;
import static java.util.Comparator.comparingInt;

/**
 * Runs the POJO Rules in a specified package as a RuleBook.
 */
public class RuleBookRunner extends AbstractRuleBookRunner {

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
   * @param ruleBookClass the RuleBook type to use as a delegate for the RuleBookRunner.
   * @param rulePackage   the package to scan for POJO rules.
   */
  public RuleBookRunner(Class<? extends RuleBook> ruleBookClass, String rulePackage) {
    super(ruleBookClass);
    _prototypeClass = ruleBookClass;
    _package = rulePackage;
  }

  /**
   * Gets the POJO Rules to be used by the RuleBook via reflection of the specified package.
   * @return  a List of POJO Rules
   */
  protected List<Class<?>> getPojoRules() {
    Reflections reflections = new Reflections(_package);

    List<Class<?>> rules = reflections
        .getTypesAnnotatedWith(com.deliveredtechnologies.rulebook.annotation.Rule.class).stream()
        .filter(rule -> rule.getAnnotatedSuperclass() != null) // Include classes only, exclude interfaces, etc.
        .collect(Collectors.toList());

    rules.sort(comparingInt(aClass ->
        getAnnotation(com.deliveredtechnologies.rulebook.annotation.Rule.class, aClass).order()));

    return rules;
  }
}
