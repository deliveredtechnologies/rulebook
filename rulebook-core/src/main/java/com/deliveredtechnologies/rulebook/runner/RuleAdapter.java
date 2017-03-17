package com.deliveredtechnologies.rulebook.runner;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Rule;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.StandardDecision;
import com.deliveredtechnologies.rulebook.annotation.Given;
import com.deliveredtechnologies.rulebook.annotation.Result;
import com.deliveredtechnologies.rulebook.annotation.Then;
import com.deliveredtechnologies.rulebook.annotation.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InvalidClassException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotatedFields;
import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotatedField;
import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotatedMethod;
import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotatedMethods;
import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotation;

/**
 * RuleAdapter accepts a POJO annotated Rule class and adapts it to an actual Rule class.
 */
public class RuleAdapter extends StandardDecision {

  private static Logger LOGGER = LoggerFactory.getLogger(RuleAdapter.class);

  private Object _ruleObj;

  /**
   * RuleAdapter accepts a {@link com.deliveredtechnologies.rulebook.annotation.Rule} annotated POJO
   * and adapts it to a {@link Rule} or {@link com.deliveredtechnologies.rulebook.Decision}.
   * @param ruleObj an annotated POJO to be adapted to a rule
   * @throws InvalidClassException  if the POJO does not have the @Rule annotation
   */
  public RuleAdapter(Object ruleObj) throws InvalidClassException {
    if (getAnnotation(com.deliveredtechnologies.rulebook.annotation.Rule.class, ruleObj.getClass()) == null) {
      throw new InvalidClassException(ruleObj.getClass() + " is not a Rule; missing @Rule annotation");
    }
    _ruleObj = ruleObj;
  }

  @Override
  @SuppressWarnings("unchecked")
  public RuleAdapter given(Fact... facts) {
    super.given(facts);
    mapGivenFactsToProperties();
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public RuleAdapter given(List list) {
    super.given(list);
    mapGivenFactsToProperties();
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public RuleAdapter given(FactMap facts) {
    super.given(facts);
    mapGivenFactsToProperties();
    return this;
  }

  @Override
  public Predicate getWhen() {
    //Use what was set by then() first, if it's there
    if (super.getWhen() != null) {
      return super.getWhen();
    }

    //If nothing was explicitly set, then convert the method in the class
    return Arrays.stream(_ruleObj.getClass().getMethods())
          .filter(method -> method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class)
          .filter(method -> Arrays.stream(method.getDeclaredAnnotations()).anyMatch(When.class::isInstance))
          .findFirst()
          .<Predicate>map(method -> object -> {
              try {
                return (Boolean) method.invoke(_ruleObj);
              } catch (InvocationTargetException | IllegalAccessException ex) {
                return false;
              }
            })
          //If the condition still can't be determined, then just had back one that returns false
         .orElse(o -> false);
  }

  /**
   * Method getThen() returns the 'then' action to be used; either a {@link Function} object, a
   * {@link BiFunction} object, or a {@link Consumer} object. If no action was specified then a default Function object
   * that returns {@link RuleState} NEXT is used.
   * @return  either a Function object or a BiFunction object
   */
  @Override
  public Object getThen() {
    if (((List<Object>)super.getThen()).size() < 1) {
      List<Object> thenList = new ArrayList<>();
      for (Method thenMethod : getAnnotatedMethods(Then.class, _ruleObj.getClass())) {
        thenMethod.setAccessible(true);
        Object then = getThenMethodAsBiConsumer(thenMethod).map(Object.class::cast)
          .orElse(getThenMethodAsConsumer(thenMethod).orElse(factMap -> {}));
        thenList.add(then);
      }
      ((List<Object>)super.getThen()).addAll(thenList);
    }
    return super.getThen();
  }

  /**
   * Convert the Facts to properties with the @Given annotation in the class.
   * If any matched properties are non-Facts, then the value of the associated Facts are mapped to those
   * properties. If any matched properties are Facts, then the Fact object are mapped to those properties.
   */
  @SuppressWarnings("unchecked")
  private void mapGivenFactsToProperties() {
    for (Field field : getAnnotatedFields(Given.class, _ruleObj.getClass())) {
      Given given = field.getAnnotation(Given.class);
      try {
        field.setAccessible(true);
        if (field.getType() == Fact.class) {
          field.set(_ruleObj, getFactMap().get(given.value()));
        } else {
          Object value = getFactMap().getValue(given.value());
          if (value != null) {
            //set the field to the Fact that has the name of the @Given value
            field.set(_ruleObj, value);
          } else if (FactMap.class == field.getType()) {
            //if the field is a FactMap then give it the FactMap
            field.set(_ruleObj, getFactMap());
          } else if (Collection.class.isAssignableFrom(field.getType())) {
            //set a Collection of Fact object values
            Stream stream = getFactMap().values().stream()
                .filter(fact -> { //filter on only facts that contain objects matching the generic type
                    ParameterizedType paramType = (ParameterizedType)field.getGenericType();
                    Class<?> genericType = (Class<?>)paramType.getActualTypeArguments()[0];
                    return genericType.equals(((Fact) fact).getValue().getClass());
                  })
                .map(fact -> {
                    ParameterizedType paramType = (ParameterizedType)field.getGenericType();
                    Class<?> genericType = (Class<?>)paramType.getActualTypeArguments()[0];
                    return genericType.cast(((Fact)fact).getValue());
                  });
            if (List.class == field.getType()) {
              //map List of Fact values to field
              field.set(_ruleObj, stream.collect(Collectors.toList()));
            } else if (Set.class == field.getType()) {
              //map Set of Fact values to field
              field.set(_ruleObj, stream.collect(Collectors.toSet()));
            }
          } else if (Map.class == field.getType()) {
            //map Map of Fact values to field
            Map map = (Map)getFactMap().keySet().stream()
                .filter(key -> {
                    ParameterizedType paramType = (ParameterizedType)field.getGenericType();
                    Class<?> genericType = (Class<?>)paramType.getActualTypeArguments()[1];
                    return genericType.equals(getFactMap().getValue((String)key).getClass());
                  })
                .collect(Collectors.toMap(key -> key, key -> getFactMap().getValue((String)key)));
            field.set(_ruleObj, map);
          }
        }
      } catch (Exception ex) {
        LOGGER.error("Unable to update field '" + field.getName() + "' in rule object '"
            + _ruleObj.getClass() + "'");
      }
    }
  }

  private Optional<BiConsumer> getThenMethodAsBiConsumer(Method method) {
    return getAnnotatedField(com.deliveredtechnologies.rulebook.annotation.Result.class, _ruleObj.getClass())
      .map(resultField -> (BiConsumer) (facts, result) -> {
        try {
          Object retVal = method.invoke(_ruleObj);
          if (method.getReturnType() == RuleState.class && retVal == RuleState.BREAK) {
            stop();
          }
          resultField.setAccessible(true);
          Object resultVal = resultField.get(_ruleObj);
          ((com.deliveredtechnologies.rulebook.Result) result).setValue(resultVal);
        } catch (IllegalAccessException | InvocationTargetException ex) {
          LOGGER.error("Unable to access " + _ruleObj.getClass().getName() + " when converting then to BiConsumer", ex);
        }
      });
  }

  private Optional<Consumer> getThenMethodAsConsumer(Method method) {
    if (!getAnnotatedField(com.deliveredtechnologies.rulebook.annotation.Result.class, _ruleObj.getClass()).isPresent()) {
      return Optional.of((Consumer) obj -> {
        try {
          Object retVal = method.invoke(_ruleObj);
          if (method.getReturnType() == RuleState.class && retVal == RuleState.BREAK) {
            stop();
          }
        }
        catch (IllegalAccessException | InvocationTargetException ex) {
          LOGGER.error("Unable to access " + _ruleObj.getClass().getName() + " when converting then to Consumer", ex);
        }
      });
    }
    return Optional.empty();
  }
}
