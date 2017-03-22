package com.deliveredtechnologies.rulebook.runner;

import com.deliveredtechnologies.rulebook.Decision;
import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.Rule;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.StandardRule;
import com.deliveredtechnologies.rulebook.annotation.Given;
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
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotatedField;
import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotatedFields;
import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotatedMethods;
import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotation;

/**
 * RuleAdapter accepts a POJO annotated Rule class and adapts it to an actual Rule class.
 */
public class RuleAdapter implements Decision {

  private static Logger LOGGER = LoggerFactory.getLogger(RuleAdapter.class);

  private Object _rulePojo;
  private Rule _rule;
  private Result _result = new Result();

  /**
   * RuleAdapter accepts a {@link com.deliveredtechnologies.rulebook.annotation.Rule} annotated POJO
   * and adapts it to a {@link Rule} or {@link com.deliveredtechnologies.rulebook.Decision}.
   * @param rulePojo  an annotated POJO to be adapted to a rule
   * @param rule      the {@link Rule} object delegated to for the adaptation
   * @throws InvalidClassException
   */
  @SuppressWarnings("unchecked")
  public RuleAdapter(Object rulePojo, Rule rule) throws InvalidClassException {
    if (getAnnotation(com.deliveredtechnologies.rulebook.annotation.Rule.class, rulePojo.getClass()) == null) {
      throw new InvalidClassException(rulePojo.getClass() + " is not a Rule; missing @Rule annotation");
    }
    _rule = rule;
    _rulePojo = rulePojo;
  }

  /**
   * RuleAdapter accepts a {@link com.deliveredtechnologies.rulebook.annotation.Rule} annotated POJO
   * and adapts it to a {@link Rule} or {@link com.deliveredtechnologies.rulebook.Decision}.
   * This convenience constructor supplies a generic {@link StandardRule} to RuleAdapter(Object, Rule).
   * @param rulePojo an annotated POJO to be adapted to a rule
   * @throws InvalidClassException  if the POJO does not have the @Rule annotation
   */
  @SuppressWarnings("unchecked")
  public RuleAdapter(Object rulePojo) throws InvalidClassException {
    this(rulePojo, new StandardRule(Object.class));
  }

  @Override
  @SuppressWarnings("unchecked")
  public Decision given(Fact... facts) {
    _rule.given(facts);
    mapGivenFactsToProperties();
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Decision given(List list) {
    _rule.given(list);
    mapGivenFactsToProperties();
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Decision given(FactMap facts) {
    _rule.given(facts);
    mapGivenFactsToProperties();
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Decision given(String name, Object value) {
    _rule.given(name, value);
    mapGivenFactsToProperties();
    return this;
  }

  @Override
  public Decision givenUnTyped(FactMap facts) {
    _rule.givenUnTyped(facts);
    mapGivenFactsToProperties();
    return this;
  }

  @Override
  public Predicate getWhen() {
    //Use what was set by then() first, if it's there
    if (_rule.getWhen() != null) {
      return _rule.getWhen();
    }

    //If nothing was explicitly set, then convert the method in the class
    return Arrays.stream(_rulePojo.getClass().getMethods())
          .filter(method -> method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class)
          .filter(method -> Arrays.stream(method.getDeclaredAnnotations()).anyMatch(When.class::isInstance))
          .findFirst()
          .<Predicate>map(method -> object -> {
              try {
                return (Boolean) method.invoke(_rulePojo);
              } catch (InvocationTargetException | IllegalAccessException ex) {
                return false;
              }
            })
          //If the condition still can't be determined, then just had back one that returns false
         .orElse(o -> false);
  }

  @Override
  public List<Object> getThen() {
    if ((_rule.getThen()).size() < 1) {
      List<Object> thenList = new ArrayList<>();
      for (Method thenMethod : getAnnotatedMethods(Then.class, _rulePojo.getClass())) {
        thenMethod.setAccessible(true);
        Object then = getThenMethodAsBiConsumer(thenMethod).map(Object.class::cast)
            .orElse(getThenMethodAsConsumer(thenMethod).orElse(factMap -> { }));
        thenList.add(then);
      }
      (_rule.getThen()).addAll(thenList);
    }
    return _rule.getThen();
  }



  @Override
  public void run(Object... otherArgs) {
    getThen();
    _rule.run(_result);
  }

  @Override
  public Decision when(Predicate test) {
    _rule.when(test);
    return this;
  }

  @Override
  public Decision then(Consumer action) {
    _rule.then(action);
    return this;
  }

  @Override
  public Decision then(BiConsumer action) {
    _rule.getThen().add(action);
    return this;
  }

  @Override
  public Decision using(String... factName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Decision stop() {
    _rule.stop();
    return this;
  }

  @Override
  public Object getResult() {
    return _result.getValue();
  }

  @Override
  public void setResult(Result result) {
    _result = result;
  }

  @Override
  public FactMap getFactMap() {
    return _rule.getFactMap();
  }

  @Override
  public void setNextRule(Rule rule) {
    _rule.setNextRule(rule);
  }

  /**
   * Convert the Facts to properties with the @Given annotation in the class.
   * If any matched properties are non-Facts, then the value of the associated Facts are mapped to those
   * properties. If any matched properties are Facts, then the Fact object are mapped to those properties.
   */
  @SuppressWarnings("unchecked")
  private void mapGivenFactsToProperties() {
    for (Field field : getAnnotatedFields(Given.class, _rulePojo.getClass())) {
      Given given = field.getAnnotation(Given.class);
      try {
        field.setAccessible(true);
        if (field.getType() == Fact.class) {
          field.set(_rulePojo, getFactMap().get(given.value()));
        } else {
          Object value = getFactMap().getValue(given.value());
          if (value != null) {
            //set the field to the Fact that has the name of the @Given value
            field.set(_rulePojo, value);
          } else if (FactMap.class == field.getType()) {
            //if the field is a FactMap then give it the FactMap
            field.set(_rulePojo, getFactMap());
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
              field.set(_rulePojo, stream.collect(Collectors.toList()));
            } else if (Set.class == field.getType()) {
              //map Set of Fact values to field
              field.set(_rulePojo, stream.collect(Collectors.toSet()));
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
            field.set(_rulePojo, map);
          }
        }
      } catch (Exception ex) {
        LOGGER.error("Unable to update field '" + field.getName() + "' in rule object '"
            + _rulePojo.getClass() + "'");
      }
    }
  }

  @SuppressWarnings("unchecked")
  private Optional<BiConsumer> getThenMethodAsBiConsumer(Method method) {
    return getAnnotatedField(com.deliveredtechnologies.rulebook.annotation.Result.class, _rulePojo.getClass())
      .map(resultField -> (BiConsumer) (facts, result) -> {
          try {
            Object retVal = method.invoke(_rulePojo);
            if (method.getReturnType() == RuleState.class && retVal == RuleState.BREAK) {
              stop();
            }
            resultField.setAccessible(true);
            Object resultVal = resultField.get(_rulePojo);
            ((com.deliveredtechnologies.rulebook.Result) result).setValue(resultVal);
          } catch (IllegalAccessException | InvocationTargetException ex) {
            LOGGER.error("Unable to access "
                + _rulePojo.getClass().getName()
                + " when converting then to BiConsumer", ex);
          }
        });
  }

  private Optional<Consumer> getThenMethodAsConsumer(Method method) {
    if (!getAnnotatedField(com.deliveredtechnologies.rulebook.annotation.Result.class,
        _rulePojo.getClass()).isPresent()) {
      return Optional.of((Consumer) obj -> {
          try {
            Object retVal = method.invoke(_rulePojo);
            if (method.getReturnType() == RuleState.class && retVal == RuleState.BREAK) {
              stop();
            }
          } catch (IllegalAccessException | InvocationTargetException ex) {
            LOGGER.error("Unable to access "
                + _rulePojo.getClass().getName()
                + " when converting then to Consumer", ex);
          }
        });
    }
    return Optional.empty();
  }
}
