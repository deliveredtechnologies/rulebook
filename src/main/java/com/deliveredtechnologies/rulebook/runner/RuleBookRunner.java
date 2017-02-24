package com.deliveredtechnologies.rulebook.runner;

import com.deliveredtechnologies.rulebook.DecisionBook;
import com.deliveredtechnologies.rulebook.annotation.Rule;
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
import java.util.stream.Stream;

/**
 * Created by clong on 2/12/17.
 * RuleBookRunner creates a RuleBook from a package containing {@link Rule} annotated POJOs.
 */
public class RuleBookRunner extends DecisionBook {

  private static Logger LOGGER = LoggerFactory.getLogger(RuleBookRunner.class);

  private String _package;

  public RuleBookRunner(String rulePackage) {
    super();
    _package = rulePackage;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void defineRules() {
    try {
      List<Class<?>> classes = findRuleClassesInPackage(_package);

      for (Class<?> rule : classes) {
        try {
          addRule(new RuleAdapter(rule.newInstance()));
        } catch (IllegalAccessException | InstantiationException ex) {
          LOGGER.error("Unable to create instance of rule using '" + rule + "'", ex);
        }
      }
    } catch (IOException | InvalidPathException ex) {
      LOGGER.error("Unable to find rule classes in package '" + _package + "'", ex);
    }
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
                if (Stream.of(ruleClass.getDeclaredAnnotations()).anyMatch(annotation -> annotation instanceof Rule)) {
                  classes.add(ruleClass);
                }
              } catch (ClassNotFoundException e) {
                LOGGER.error("Unable to resolve class for '" + packageName + "." + className + "'", e);
              }
            });
      classes.sort(
          (class1, class2) -> class1.getAnnotation(Rule.class).order() - class2.getAnnotation(Rule.class).order());

      return classes;
    } catch (URISyntaxException ex) {
      throw new InvalidPathException("'" + packageName + "' is not a valid path", ex.getReason());
    }
  }
}
