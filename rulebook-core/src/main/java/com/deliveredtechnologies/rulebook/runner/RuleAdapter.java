package com.deliveredtechnologies.rulebook.runner;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Rule;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.StandardDecision;
import com.deliveredtechnologies.rulebook.annotation.Given;
import com.deliveredtechnologies.rulebook.annotation.Then;
import com.deliveredtechnologies.rulebook.annotation.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InvalidClassException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    if (getAnnotation(ruleObj.getClass(), com.deliveredtechnologies.rulebook.annotation.Rule.class) == null) {
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
   * Method getThen() returns the 'then' action to be used; either a {@link Function} object or a
   * {@link BiFunction} object. If no action was specified then a default Function object that returns
   * {@link RuleState} NEXT is used.
   * @return  either a Function object or a BiFunction object
   */
  public Object getThen() {
    //Use what was explicitly set by then() first
    if (super.getThen() != null) {
      return super.getThen();
    }

    //If nothing was explicitly set, convert what's in the class to a BiFunction or a Function
    //If the action still can't be determined then just give back a Function that moves down the chain
    return getThenMethodAsBiFunction().map(Object.class::cast)
        .orElse(getThenMethodAsFunction().orElse(factMap -> RuleState.NEXT));
  }

  /**
   * Convert the Facts to properties with the @Given annotation in the class.
   * If any matched properties are non-Facts, then the value of the associated Facts are mapped to those
   * properties. If any matched properties are Facts, then the Fact object are mapped to those properties.
   */
  @SuppressWarnings("unchecked")
  private void mapGivenFactsToProperties() {
    for (Field field : _ruleObj.getClass().getDeclaredFields()) {
      Given given = field.getAnnotation(Given.class);
      if (given != null) {
        try {
          field.setAccessible(true);
          if (field.getType() == Fact.class) {
            field.set(_ruleObj, getFactMap().get(given.value()));
          } else {
            Object value = getFactMap().getValue(given.value());
            if (value != null) { //set the field to the Fact that has the name of the @Given value
              field.set(_ruleObj, value);
            } else if (FactMap.class == field.getType()) { //if the field is a FactMap then give it the FactMap
              field.set(_ruleObj, getFactMap());
            } else if (Collection.class.isAssignableFrom(field.getType())) { //set a Collection of Fact object values
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
              if (List.class == field.getType()) { //Collection type is List
                field.set(_ruleObj, stream.collect(Collectors.toList()));
              } else if (Set.class == field.getType()) { //Collection type is Set
                field.set(_ruleObj, stream.collect(Collectors.toSet()));
              }
            } else if (Map.class == field.getType()) { //Collection type is Map
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
          try {
            field.set(_ruleObj, null);
          } catch (IllegalAccessException iax) {
            LOGGER.error("Unable to update field '" + field.getName() + "' in rule object '"
                + _ruleObj.getClass() + "'", iax);
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
    return getResultField(_ruleObj).flatMap(resultField -> Arrays.stream(_ruleObj.getClass().getMethods())
        .filter(method -> Arrays.stream(method.getAnnotations()).anyMatch(Then.class::isInstance)).findFirst()
        .map(method -> toBiFunction(method, resultField)));
  }

  private BiFunction toBiFunction(Method method, Field resultField) {
    return (factMap, resultObj) -> {
      try {
        Object retVal = method.invoke(_ruleObj);
        resultField.setAccessible(true);
        Object resultVal = resultField.get(_ruleObj);
        if (resultVal != null) {
          ((com.deliveredtechnologies.rulebook.Result) resultObj).setValue(resultVal);
        }
        return retVal;
      } catch (IllegalAccessException | InvocationTargetException ex) {
        return RuleState.BREAK;
      }
    };
  }

  /**
   * Method getThenMethodAsFunction returns a Function when a @Then annotated method exists without a @Result annotated
   * property.
   * @return  a {@link Function} from the @Then annotated method of the obejct
   */
  private Optional<Function> getThenMethodAsFunction() {
    if (getResultField(_ruleObj).isPresent()) {
      return Optional.empty();
    }
    return Arrays.stream(_ruleObj.getClass().getMethods())
        .filter(method -> Arrays.stream(method.getAnnotations()).anyMatch(Then.class::isInstance))
        .findFirst()
        .map(method -> obj -> {
            try {
              return method.invoke(_ruleObj);
            } catch (IllegalAccessException | InvocationTargetException ex) {
              return RuleState.BREAK;
            }
          });
  }

  /**
   * Method getResultField is a utility method that gets the @Result annotated property when applicable.
   * @param obj an object that is annotated and converts to a Rule
   * @return  an Optional Field object containing either nothing or the property annotated with @Result
   */
  private static Optional<Field> getResultField(Object obj) {
    return Stream.of(obj.getClass().getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(com.deliveredtechnologies.rulebook.annotation.Result.class))
        .findFirst();
  }

  /**
   * Method getAnnotation returns the annotation on a class or its parent annotation.
   * @param clazz       the annotated class
   * @param annotation  the annotation to find
   * @param <A>         the type of the annotation
   * @return            the actual annotation used or null if it doesn't exist
   */
  private static <A extends Annotation> A getAnnotation(Class<?> clazz, Class<A> annotation) {
    return Optional.ofNullable(clazz.getAnnotation(annotation)).orElse((A)
      Arrays.stream(clazz.getDeclaredAnnotations())
        .flatMap(anno -> Arrays.stream(anno.getClass().getInterfaces())
          .flatMap(iface -> Arrays.stream(iface.getDeclaredAnnotations())))
        .filter(pAnno -> annotation.isInstance(pAnno))
        .findFirst().orElse(null)
    );
  }
}
