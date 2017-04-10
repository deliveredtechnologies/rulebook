package com.deliveredtechnologies.rulebook.model.runner;

import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotatedField;
import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotation;

/**
 * Runs the POJO Rules in a specified package as a RuleBook.
 */
public class RuleBookRunner implements RuleBook {

  private static Logger LOGGER = LoggerFactory.getLogger(RuleBookRunner.class);

  private RuleBook _ruleBook;
  private String _package;

  /**
   * Creates a new RuleBookRunner using the specified package and the default RuleBook.
   * @param rulePackage a package to scan for POJO Rules
   */
  public RuleBookRunner(String rulePackage) {
    this(new CoRRuleBook(), rulePackage);
  }

  /**
   * Creates a new RuleBookRunner using the specified package and the supplied RuleBook.
   * @param ruleBook    the RuleBook to use as a delegate for the RuleBookRunner
   * @param rulePackage the package to scan for POJO rules
   */
  public RuleBookRunner(RuleBook ruleBook, String rulePackage) {
    _ruleBook = ruleBook;
    _package = rulePackage;
  }

  @Override
  public void addRule(Rule rule) {
    _ruleBook.addRule(rule);
  }

  @Override
  public void run(NameValueReferableMap facts) {
    if (!hasRules()) {
      defineRules();
    }
    _ruleBook.run(facts);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void setDefaultResult(Object result) {
    _ruleBook.setDefaultResult(result);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<Result> getResult() {
    return _ruleBook.getResult();
  }

  /**
   * Define the Rules in the RuleBook as Rule annotated POJO Rules in the specified package.
   */
  @Override
  public void defineRules() {
    try {
      List<Class<?>> classes = findRuleClassesInPackage(_package);
      for (Class<?> rule : classes) {
        try {
          getAnnotatedField(com.deliveredtechnologies.rulebook.annotation.Result.class, rule).ifPresent(field -> {
              if (!getResult().isPresent()) {
                setDefaultResult(new Object());
              }
            });
          addRule(new RuleAdapter(rule.newInstance()));
        } catch (IllegalAccessException | InstantiationException ex) {
          LOGGER.warn("Unable to create instance of rule using '" + rule + "'", ex);
        }
      }
    } catch (IOException | InvalidPathException ex) {
      LOGGER.error("Unable to find rule classes in package '" + _package + "'", ex);
    }
  }

  @Override
  public boolean hasRules() {
    return _ruleBook.hasRules();
  }

  private List<Class<?>> findRuleClassesInPackage(String packageName) throws InvalidPathException, IOException {
    String pathName = packageName.replace(".", "/");
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    URL pathUrl = classLoader.getResource(pathName);
    if (pathUrl == null) {
      throw new InvalidPathException("'" + packageName + "' cannot be found by the ClassLoader", packageName);
    }
    try {
      Path path = Paths.get(pathUrl.toURI());
      if (!Files.exists(path) || !Files.isDirectory(path)) {
        throw new InvalidPathException("'" + packageName + "' is not a valid path", packageName);
      }

      List<Class<?>> classes = new ArrayList<>();
      Files.walk(path, 1)
              .filter(p -> !Files.isDirectory(p))
              .forEach(p -> {
                  String fileName = p.getFileName().toString();
                  String className = fileName.substring(0, fileName.length() - 6);
                  try {
                    Class<?> ruleClass = Class.forName(packageName + "." + className);
                    if (getAnnotation(com.deliveredtechnologies.rulebook.annotation.Rule.class, ruleClass) != null) {
                      classes.add(ruleClass);
                    }
                  } catch (ClassNotFoundException e) {
                    LOGGER.error("Unable to resolve class for '" + packageName + "." + className + "'", e);
                  }
                });
      classes.sort((class1, class2) ->
                      getAnnotation(com.deliveredtechnologies.rulebook.annotation.Rule.class, class1).order()
                      - getAnnotation(com.deliveredtechnologies.rulebook.annotation.Rule.class, class2).order());

      return classes;
    } catch (URISyntaxException ex) {
      throw new InvalidPathException("'" + packageName + "' is not a valid path", ex.getReason());
    }
  }
}
