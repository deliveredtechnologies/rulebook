package com.deliveredtechnologies.rulebook.model.runner;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class PojoRulesCache {

  private static Logger LOGGER = LoggerFactory.getLogger(PojoRulesCache.class);

  private static final Map<String, List<Class<?>>> CACHE = new ConcurrentHashMap<>();

  static List<Class<?>> getPojoRules(final String packageName) {
    if (CACHE.containsKey(packageName))
      return CACHE.get(packageName);

    return updateCache(packageName);
  }

  private static List<Class<?>> updateCache(final String packageName) {
    try (ScanResult scanResult =
             new ClassGraph()
                 .enableAllInfo()
                 .whitelistPackages(packageName)
                 .scan()) {
      List<Class<?>> classes = scanResult.getAllClasses()
              .filter(classInfo -> classInfo.hasAnnotation("com.deliveredtechnologies.rulebook.annotation.Rule")).loadClasses();

      CACHE.put(packageName, classes);
      return classes;
    }
  }
}
