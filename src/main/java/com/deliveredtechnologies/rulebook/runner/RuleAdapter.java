package com.deliveredtechnologies.rulebook.runner;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.StandardDecision;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.Rule;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.Given;
import com.deliveredtechnologies.rulebook.annotation.Then;
import com.deliveredtechnologies.rulebook.annotation.When;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by clong on 2/12/17.
 * RuleAdapter accepts a POJO annotated Rule class and adapts it to an actual Rule class.
 */
public class RuleAdapter extends StandardDecision {
  private Object _ruleObj;

  public RuleAdapter(Object ruleObj) {
    _ruleObj = ruleObj;
  }

  @Override
  public RuleAdapter given(Fact... facts) {
    super.given(facts);
    mapGivenFactsToProperties();
    return this;
  }

  @Override
  public RuleAdapter given(List list) {
    super.given(list);
    mapGivenFactsToProperties();
    return this;
  }

  @Override
  public RuleAdapter given(FactMap facts) {
    super.given(facts);
    mapGivenFactsToProperties();
    return this;
  }

  @Override
  public Predicate getWhen() {
    //Use what was set by then() first, if it's there
    if (Optional.ofNullable(super.getWhen()).isPresent()) {
      return super.getWhen();
    }

    //If nothing was explicitly set, then convert the method in the class
    for (Method method : _ruleObj.getClass().getMethods()) {
      for (Annotation annotation : method.getDeclaredAnnotations()) {
        if (annotation instanceof When &&
          (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class)) {
          return new Predicate() {
            @Override
            public boolean test(Object obj) {
              try {
                return (Boolean)method.invoke(_ruleObj);
              } catch (InvocationTargetException | IllegalAccessException ex) {
                return false;
              }
            }
          };
        }
      }
    }

    //If the condition still can't be determined, then just had back one that returns false
    return new Predicate() {
      @Override
      public boolean test(Object obj) {
        return false;
      }
    };
  }

  public Object getThen() {
    //Use what was explicitly set by then() first
    if (Optional.ofNullable(super.getThen()).isPresent()) {
      return super.getThen();
    }

    //If nothing was explicitly set, convert what's in the class to a BiFunction or a Function
    Optional<BiFunction> biFunction = getThenMethodAsBiFunction();
    if (biFunction.isPresent()) {
      return biFunction.get();
    }

    Optional<Function> function = getThenMethodAsFunction();
    if (function.isPresent()) {
      return function.get();
    }
    //If the action still can't be determined then just give back a Function that moves down the chain
    return new Function<FactMap, RuleState>() {
      @Override
      public RuleState apply(FactMap factMap) {
        return RuleState.NEXT;
      }
    };
  }

  /**
   * Convert the Facts to properties with the @Given annotation in the class
   */
  private void mapGivenFactsToProperties() {
    for (Field field : _ruleObj.getClass().getDeclaredFields()) {
      for (Annotation annotation : field.getDeclaredAnnotations()) {
        if (annotation instanceof Given) {
          Given given = (Given)annotation;
          try {
            field.setAccessible(true);
            if (field.getType() == Fact.class) {
              field.set(_ruleObj, getFactMap().get(given.value()));
            } else {
              try {
                Object value = getFactMap().getValue(given.value());
                if (Optional.ofNullable(value).isPresent()) {
                  field.set(_ruleObj, value);
                }
              } catch (Exception ex) {
                field.set(_ruleObj, null);
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

  /**
   * Method getThenMethodAsBiFunction returns a BiFunction using the annotated object, when a
   * @Result annotated property and a @Then annotated method exists.
   * @return  A {@link BiFunction} for classes that have a @Then method and a @Result
   */
  private Optional<BiFunction> getThenMethodAsBiFunction() {
    for (Method method : _ruleObj.getClass().getMethods()) {
      for (Annotation annotation : method.getAnnotations()) {
        Optional<Field> resultField = getResultField(_ruleObj);
        if (annotation instanceof Then && resultField.isPresent()) {
          return Optional.of(new BiFunction() {
            @Override
            public Object apply(Object factMap, Object resultObj) {
              try {
                Object retVal = method.invoke(_ruleObj);
                resultField.get().setAccessible(true);
                Object resultVal = resultField.get().get(_ruleObj);
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

  /**
   * Method getThenMethodAsFunction returns a Function when a @Then annotated method exists without a @Result annotated
   * property.
   * @return  a {@link Function} from the @Then annotated method of the obejct
   */
  private Optional<Function> getThenMethodAsFunction() {
    for (Method method : _ruleObj.getClass().getMethods()) {
      for (Annotation annotation : method.getAnnotations()) {
        if (annotation instanceof Then && !getResultField(_ruleObj).isPresent()) {
          return Optional.of(new Function() {
            @Override
            public Object apply(Object o) {
              try {
                return method.invoke(_ruleObj);
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

  /**
   * Method getResultField is a utility method that gets the @Result annotated property when applicable
   * @param obj an object that is annotated and converts to a Rule
   * @return  an Optional Field object containing either nothing or the property annotated with @Result
   */
  private static Optional<Field> getResultField(Object obj) {
    return Stream.of(obj.getClass().getDeclaredFields())
      .filter(field ->
        Optional.ofNullable(field.getAnnotation(com.deliveredtechnologies.rulebook.annotation.Result.class)).isPresent()
      )
      .findFirst();
  }
}
