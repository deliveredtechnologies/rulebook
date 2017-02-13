package com.deliveredtechnologies.rulebook.runner;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.Given;
import com.deliveredtechnologies.rulebook.annotation.Result;
import com.deliveredtechnologies.rulebook.annotation.Then;
import com.deliveredtechnologies.rulebook.annotation.When;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by clong on 2/12/17.
 */
public class Util {
  private Util() {}

  public static Predicate getWhenMethodAsPredicate(Object obj) {
    for (Method method : obj.getClass().getMethods()) {
      for (Annotation annotation : method.getDeclaredAnnotations()) {
        if (annotation instanceof When && method.getReturnType() == Boolean.class) {
          return new Predicate() {
            @Override
            public boolean test(Object o) {
              try {
                return (Boolean)method.invoke(obj);
              } catch (InvocationTargetException | IllegalAccessException ex) {
                return false;
              }
            }
          };
        }
      }
    }
    return new Predicate() {
      @Override
      public boolean test(Object o) {
        return false;
      }
    };
  }

  public static void mapGivenFactsToProperties(Object obj, FactMap factMap) {
    for (Field field : obj.getClass().getDeclaredFields()) {
      for (Annotation annotation : field.getDeclaredAnnotations()) {
        if (annotation instanceof Given) {
          Given given = (Given)annotation;
          try {
            field.set(obj, factMap.getValue(given.name()));
          }
          catch (IllegalAccessException ex) {
            //TODO: handle error
          }
        }
      }
    }
  }

  public static BiFunction getThenMethodAsBiFunction(Object obj) {
    for (Method method : obj.getClass().getMethods()) {
      for (Annotation annotation : method.getAnnotations()) {
        if (annotation instanceof Then && hasResult(obj)) {
          return new BiFunction() {
            @Override
            public Object apply(Object o, Object o2) {
              try {
                return method.invoke(obj);
              } catch (IllegalAccessException | InvocationTargetException ex) {
                return RuleState.BREAK;
              }
            }
          };
        }
      }
    }
    return new BiFunction() {
      @Override
      public Object apply(Object o, Object o2) {
        return RuleState.BREAK;
      }
    };
  }

  public static Function getThenMethodAsFunction(Object obj) {
    for (Method method : obj.getClass().getMethods()) {
      for (Annotation annotation : method.getAnnotations()) {
        if (annotation instanceof Then && !hasResult(obj)) {
          return new Function() {
            @Override
            public Object apply(Object o) {
              try {
                return method.invoke(obj);
              } catch (IllegalAccessException | InvocationTargetException ex) {
                return RuleState.BREAK;
              }
            }
          };
        }
      }
    }
    return new Function() {
      @Override
      public Object apply(Object o) {
        return RuleState.BREAK;
      }
    };
  }

  private static boolean hasResult(Object obj) {
    return Stream.of(obj.getClass().getDeclaredFields())
      .anyMatch(field -> Stream.of(field.getDeclaredAnnotations())
        .anyMatch(annotation -> annotation.getClass() == Result.class)
      );
  }
}
