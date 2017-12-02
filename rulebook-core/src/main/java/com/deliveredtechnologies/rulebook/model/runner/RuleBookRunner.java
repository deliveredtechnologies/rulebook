package com.deliveredtechnologies.rulebook.model.runner;

import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.Auditable;
import com.deliveredtechnologies.rulebook.model.AuditableRule;
import com.deliveredtechnologies.rulebook.model.Auditor;
import com.deliveredtechnologies.rulebook.model.Rule;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotatedField;
import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotation;

/**
 * Runs the POJO Rules in a specified package as a RuleBook.
 */
public class RuleBookRunner extends Auditor implements RuleBook {

  private static Logger LOGGER = LoggerFactory.getLogger(RuleBookRunner.class);

  private String _package;
  private Class<? extends RuleBook> _prototypeClass;

  @SuppressWarnings("unchecked")
  private Result _result = new Result(null);

  private List<Class<?>> _pojoClassesList;

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

  /**
   * Creates a new RuleBookRunner using specified pojo Rule classes.
   *
   * @param pojoClassesList the list of pojo classes.
   */
  public RuleBookRunner(List<Class<?>> pojoClassesList) {
    this(CoRRuleBook.class, null);
    _pojoClassesList = pojoClassesList;
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
      return findRuleClassesInPackage(_package).size() > 0;
    } catch (IOException | InvalidPathException e) {
      LOGGER.error("Unable to find rule classes", e);
      return false;
    }
  }

  private List<Class<?>> findRuleClassesInPackage(String packageName) throws InvalidPathException, IOException {
    if (packageName != null && !packageName.isEmpty()) {
      String pathName = packageName.replace(".", "/");
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      URL pathUrl = classLoader.getResource(pathName);
      if (pathUrl == null) {
        throw new InvalidPathException("'" + packageName + "' cannot be found by the ClassLoader", packageName);
      }

      FileSystem fs = null;
      Path path;
      try {
        URI pathUri = pathUrl.toURI();
        LOGGER.debug(String.format("%s URI -> %s", pathName, pathUrl.toURI()));

        if (pathUri.toString().contains("!")) {

          //if it's inside an archive, reference the archive as the FileSystem and combine the remaining paths
          String[] paths = pathUri.toString().split("!");
          fs = FileSystems.newFileSystem(URI.create(paths[0]), new HashMap<>());
          String strPath = Arrays.stream(Arrays.copyOfRange(paths, 1, paths.length))
              .reduce((item1, item2) -> item1 + item2).get();

          LOGGER.debug(String.format("Resource Path Inside Archive: %s", strPath));
          path = fs.getPath(strPath);
        } else {

          //if it's not inside an archive, then just use the path based on the current FileSystem
          path = Paths.get(pathUri);
        }

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
      } finally {
        if (fs != null) {
          fs.close();
        }
      }
    } else if (_pojoClassesList != null && !_pojoClassesList.isEmpty()) {
      return _pojoClassesList;
    }
    return new ArrayList<>();
  }
}
