package com.deliveredtechnologies.rulebook.runner;

import com.deliveredtechnologies.rulebook.Fact;
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
import java.util.Optional;
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
        if (annotation instanceof When &&
          (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class)) {
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
            field.setAccessible(true);
            if (field.getType() == Fact.class) {
              field.set(obj, factMap.get(given.value()));
            } else {
              try {
                field.set(obj, factMap.getValue(given.value()));
              } catch (Exception ex) {
                field.set(obj, null);
              }
            }
          }
          catch (IllegalAccessException ex) {
            //TODO: handle error
          }
        }
      }
    }
  }

  public static Optional<BiFunction> getThenMethodAsBiFunction(Object obj) {
    for (Method method : obj.getClass().getMethods()) {
      for (Annotation annotation : method.getAnnotations()) {
        Optional<Field> resultField = getResultField(obj);
        if (annotation instanceof Then && resultField.isPresent()) {
          return Optional.of(new BiFunction() {
            @Override
            public Object apply(Object factMap, Object resultObj) {
              try {
                Object retVal = method.invoke(obj);
                resultField.get().setAccessible(true);
                Object resultVal = resultField.get().get(obj);
                com.deliveredtechnologies.rulebook.Result result = (com.deliveredtechnologies.rulebook.Result)resultObj;
                if (Optional.ofNullable(resultVal).isPresent()) {
                  result.setValue(resultVal);
                }
                return retVal;
              } catch (IllegalAccessException | InvocationTargetException ex) {
                return RuleState.BREAK;
              }
            }
          });
        }
      }
    }
    return Optional.empty();
  }

  public static Optional<Function> getThenMethodAsFunction(Object obj) {
    for (Method method : obj.getClass().getMethods()) {
      for (Annotation annotation : method.getAnnotations()) {
        if (annotation instanceof Then && !getResultField(obj).isPresent()) {
          return Optional.of(new Function() {
            @Override
            public Object apply(Object o) {
              try {
                return method.invoke(obj);
              } catch (IllegalAccessException | InvocationTargetException ex) {
                return RuleState.BREAK;
              }
            }
          });
        }
      }
    }
    return Optional.empty();
  }

  private static Optional<Field> getResultField(Object obj) {
    return Stream.of(obj.getClass().getDeclaredFields())
      .filter(field -> Stream.of(field.getDeclaredAnnotations())
        .allMatch(annotation -> annotation.getClass() == Result.class)).findFirst();
  }
}
