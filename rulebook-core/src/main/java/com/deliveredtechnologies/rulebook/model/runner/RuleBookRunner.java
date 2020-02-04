package com.deliveredtechnologies.rulebook.model.runner;

import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotation;
import static java.util.Comparator.comparingInt;

/**
 * Runs the POJO Rules in a specified package as a RuleBook.
 */
public class RuleBookRunner extends AbstractRuleBookRunner {

  private static Logger LOGGER = LoggerFactory.getLogger(RuleBookRunner.class);

  private String _package;
  private Predicate<String> _subPkgMatch;
  private Class<? extends RuleBook> _prototypeClass;
  private ReentrantReadWriteLock _lock = new ReentrantReadWriteLock();
  private Optional<List<Class<?>>> _rules = Optional.ofNullable(null);

  @SuppressWarnings("unchecked")
  private Result _result = new Result(null);

  /**
   * Creates a new RuleBookRunner using the specified package and the default RuleBook.
   * @param rulePackage a package to scan for POJO Rules
   */
  public RuleBookRunner(String rulePackage) {
    this(rulePackage, s -> s.startsWith(rulePackage));
  }

  /**
   * Creates a new RuleBookRunner using the specified package and the default RuleBook.
   * @param rulePackage a package to scan for POJO Rules
   * @param subPkgMatch Predicate that returns true for any subpackages that will be scanned
   */
  public RuleBookRunner(String rulePackage, Predicate<String> subPkgMatch) {
    this(CoRRuleBook.class, rulePackage, subPkgMatch);
  }

  /**
   * Creates a new RuleBookRunner using the specified package and the supplied RuleBook.
   * @param ruleBookClass the RuleBook type to use as a delegate for the RuleBookRunner
   * @param rulePackage   the package to scan for POJO rules
   */
  public RuleBookRunner(Class<? extends RuleBook> ruleBookClass, String rulePackage) {
    this(ruleBookClass, rulePackage, s -> s.startsWith(rulePackage));
  }

  /**
   * Creates a new RuleBookRunner using the specified package, the supplied RuleBook and matching subpackages Predicate.
   * @param ruleBookClass the RuleBook type to use as a delegate for the RuleBookRunner
   * @param rulePackage   the package to scan for POJO rules
   * @param subPkgMatch   Predicate that returns true for any subpackages that will be scanned
   */
  public RuleBookRunner(Class<? extends RuleBook> ruleBookClass, String rulePackage, Predicate<String> subPkgMatch) {
    super(ruleBookClass);
    _prototypeClass = ruleBookClass;
    _package = rulePackage;
    _subPkgMatch = subPkgMatch;
  }

  /**
   * Gets the POJO Rules to be used by the RuleBook via reflection of the specified package.
   * @return  a List of POJO Rules
   */
  protected List<Class<?>> getPojoRules() {
    Reflections reflections = new Reflections(_package);


    try {
      _lock.readLock().lock();
      if (_rules.isPresent()) {
        return _rules.get();
      }
    } finally {
      _lock.readLock().unlock();
    }
    try {
      _lock.writeLock().lock();
      if (_rules.isPresent()) {
        return _rules.get();
      }
      List<Class<?>> rules = reflections
          .getTypesAnnotatedWith(com.deliveredtechnologies.rulebook.annotation.Rule.class).stream()
          .filter(rule -> rule.getAnnotatedSuperclass() != null) // Include classes only, exclude interfaces, etc.
          .filter(rule -> _subPkgMatch.test(rule.getPackage().getName()))
          .collect(Collectors.toList());

      rules.sort(comparingInt(aClass ->
          getAnnotation(com.deliveredtechnologies.rulebook.annotation.Rule.class, aClass).order()));
      _rules = Optional.of(rules);
    } finally {
      _lock.writeLock().unlock();
    }
    return _rules.get();
  }
}
