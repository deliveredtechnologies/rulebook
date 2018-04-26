package com.deliveredtechnologies.rulebook.model.runner;



import com.deliveredtechnologies.rulebook.NameValueReferable;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.Given;
import com.deliveredtechnologies.rulebook.annotation.Then;
import com.deliveredtechnologies.rulebook.annotation.When;
import com.deliveredtechnologies.rulebook.model.GoldenRule;
import com.deliveredtechnologies.rulebook.model.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InvalidClassException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Arrays;
import java.util.Optional;
import java.util.Collection;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotatedField;
import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotatedMethods;
import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotatedFields;
import static com.deliveredtechnologies.rulebook.util.AnnotationUtils.getAnnotation;
/**
 * Adapts a {@link com.deliveredtechnologies.rulebook.annotation.Rule} annotated POJO to a
 * {@link Rule}.
 */
public class RuleAdapter implements Rule {

  private static Logger LOGGER = LoggerFactory.getLogger(RuleAdapter.class);

  private Rule _rule;
  private Object _pojoRule;

  /**
   * Adapts a POJO to a Rule given a POJO and a Rule.
   * @param pojoRule                the @Rule annotated POJO
   * @param rule                    a Rule object
   * @throws InvalidClassException  if the @Rule annotation is missing from the POJO
   */
  public RuleAdapter(Object pojoRule, Rule rule) throws InvalidClassException {
    if (getAnnotation(com.deliveredtechnologies.rulebook.annotation.Rule.class, pojoRule.getClass()) == null) {
      throw new InvalidClassException(pojoRule.getClass() + " is not a Rule; missing @Rule annotation");
    }
    _rule = rule;
    _pojoRule = pojoRule;
  }

  /**
   * Adapts a POJO to a the default Rule type (i.e. {@link GoldenRule}).
   * @param pojoRule                the @Rule annotated POJO
   * @throws InvalidClassException  if the @Rule annotation is missing from the POJO
   */
  @SuppressWarnings("unchecked")
  public RuleAdapter(Object pojoRule) throws InvalidClassException {
    this(pojoRule, new GoldenRule(Object.class));
  }

  @Override
  public void addFacts(NameValueReferable... facts) {
    _rule.addFacts(facts);
    mapFactsToProperties(_rule.getFacts());
  }

  @Override
  public void addFacts(NameValueReferableMap facts) {
    _rule.addFacts(facts);
    mapFactsToProperties(_rule.getFacts());
  }

  @Override
  public void setFacts(NameValueReferableMap facts) {
    _rule.setFacts(facts);
    mapFactsToProperties(_rule.getFacts());
  }

  @Override
  @SuppressWarnings("unchecked")
  public void setCondition(Predicate condition) throws IllegalStateException {
    _rule.setCondition(condition);
  }

  @Override
  public void setRuleState(RuleState ruleState) {
    _rule.setRuleState(ruleState);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void addAction(Consumer action) {
    _rule.addAction(action);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void addAction(BiConsumer action) {
    _rule.addAction(action);
  }

  @Override
  public void addFactNameFilter(String... factNames) {
    throw new UnsupportedOperationException();
  }

  @Override
  public NameValueReferableMap getFacts() {
    return _rule.getFacts();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Predicate<NameValueReferableMap> getCondition() {
    //Use what was set by then() first, if it's there
    if (_rule.getCondition() != null) {
      return _rule.getCondition();
    }

    //If nothing was explicitly set, then convert the method in the class
    _rule.setCondition(Arrays.stream(_pojoRule.getClass().getMethods())
            .filter(method -> method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class)
            .filter(method -> Arrays.stream(method.getDeclaredAnnotations()).anyMatch(When.class::isInstance))
            .findFirst()
            .<Predicate>map(method -> object -> {
              try {
                return (Boolean) method.invoke(_pojoRule);
              } catch (InvocationTargetException | IllegalAccessException ex) {
                LOGGER.error(
                    "Unable to validate condition due to an exception. Condition will be interpreted as false", ex);
                return false;
              }
            })
            //If the condition still can't be determined, then just hand back one that returns true
            .orElse(o -> true));
    return _rule.getCondition();
  }

  @Override
  public RuleState getRuleState() {
    return _rule.getRuleState();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Object> getActions() {
    if (_rule.getActions().size() < 1) {
      List<Object> actionList = new ArrayList<>();
      for (Method actionMethod : getAnnotatedMethods(Then.class, _pojoRule.getClass())) {
        actionMethod.setAccessible(true);
        Object then = getThenMethodAsBiConsumer(actionMethod).map(Object.class::cast)
            .orElse(getThenMethodAsConsumer(actionMethod).orElse(factMap -> { }));
        actionList.add(then);
      }
      _rule.getActions().addAll(actionList);
    }
    return _rule.getActions();
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean invoke(NameValueReferableMap facts) {
    mapFactsToProperties(facts);
    // getActions and getCondition are called here so that they could be overridden prior to calling invoke()
    getActions();
    getCondition();
    return _rule.invoke(facts);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void setResult(Result result) {
    _rule.setResult(result);

    getAnnotatedField(com.deliveredtechnologies.rulebook.annotation.Result.class, _pojoRule.getClass())
          .ifPresent(field -> {
            field.setAccessible(true);
            try {
              if (result.getValue().getClass() != Object.class
                  || field.getType().isAssignableFrom(result.getValue().getClass())) {
                field.set(_pojoRule, result.getValue());
              }
            } catch (Exception ex) {
              LOGGER.error("Unable to set @Result field in " + _pojoRule.getClass(), ex);
            }
          });
  }

  @Override
  public Optional<Result> getResult() {
    return _rule.getResult();
  }

  /**
   * Convert the Facts to properties with the @Given annotation in the class.
   * If any matched properties are non-Facts, then the value of the associated Facts are mapped to those
   * properties. If any matched properties are Facts, then the Fact object are mapped to those properties.
   */
  @SuppressWarnings("unchecked")
  private void mapFactsToProperties(NameValueReferableMap facts) {
    for (Field field : getAnnotatedFields(Given.class, _pojoRule.getClass())) {
      Given given = field.getAnnotation(Given.class);
      try {
        field.setAccessible(true);
        if (NameValueReferable.class.isAssignableFrom(field.getType())) {
          field.set(_pojoRule, facts.get(given.value()));
        } else {
          Object value = facts.getValue(given.value());
          if (value != null) {
            //set the field to the Fact that has the name of the @Given value
            field.set(_pojoRule, value);
          } else if (NameValueReferableMap.class.isAssignableFrom(field.getType())) {
            //if the field is a FactMap then give it the FactMap
            field.set(_pojoRule, facts);
          } else if (Collection.class.isAssignableFrom(field.getType())) {
            //set a Collection of Fact object values
            Stream stream = facts.values().stream()
                    .filter(fact -> { //filter on only facts that contain objects matching the generic type
                      ParameterizedType paramType = (ParameterizedType)field.getGenericType();
                      Class<?> genericType = (Class<?>)paramType.getActualTypeArguments()[0];
                      return genericType.equals(((NameValueReferable) fact).getValue().getClass());
                    })
                    .map(fact -> {
                      ParameterizedType paramType = (ParameterizedType)field.getGenericType();
                      Class<?> genericType = (Class<?>)paramType.getActualTypeArguments()[0];
                      return genericType.cast(((NameValueReferable)fact).getValue());
                    });
            if (List.class == field.getType()) {
              //map List of Fact values to field
              field.set(_pojoRule, stream.collect(Collectors.toList()));
            } else if (Set.class == field.getType()) {
              //map Set of Fact values to field
              field.set(_pojoRule, stream.collect(Collectors.toSet()));
            }
          } else if (Map.class == field.getType()) {
            //map Map of Fact values to field
            Map map = (Map)facts.keySet().stream()
                    .filter(key -> {
                      ParameterizedType paramType = (ParameterizedType)field.getGenericType();
                      Class<?> genericType = (Class<?>)paramType.getActualTypeArguments()[1];
                      return genericType.equals(facts.getValue((String)key).getClass());
                    })
                    .collect(Collectors.toMap(key -> key, key -> facts.getValue((String)key)));
            field.set(_pojoRule, map);
          }
        }
      } catch (Exception ex) {
        LOGGER.error("Unable to update field '" + field.getName() + "' in rule object '"
                + _pojoRule.getClass() + "'", ex);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private Optional<BiConsumer> getThenMethodAsBiConsumer(Method method) {
    return getAnnotatedField(com.deliveredtechnologies.rulebook.annotation.Result.class, _pojoRule.getClass())
            .map(resultField -> (BiConsumer) (facts, result) -> {
              try {
                Object retVal = method.invoke(_pojoRule);
                if (method.getReturnType() == RuleState.class && retVal == RuleState.BREAK) {
                  _rule.setRuleState(RuleState.BREAK);
                }
                resultField.setAccessible(true);
                Object resultVal = resultField.get(_pojoRule);
                ((com.deliveredtechnologies.rulebook.Result) result).setValue(resultVal);
              } catch (IllegalAccessException | InvocationTargetException ex) {
                LOGGER.error("Unable to access "
                      + _pojoRule.getClass().getName()
                      + " when converting then to BiConsumer", ex);
              }
            });
  }

  private Optional<Consumer> getThenMethodAsConsumer(Method method) {
    if (!getAnnotatedField(com.deliveredtechnologies.rulebook.annotation.Result.class,
            _pojoRule.getClass()).isPresent()) {
      return Optional.of((Consumer) obj -> {
        try {
          Object retVal = method.invoke(_pojoRule);
          if (method.getReturnType() == RuleState.class && retVal == RuleState.BREAK) {
            _rule.setRuleState(RuleState.BREAK);
          }
        } catch (IllegalAccessException | InvocationTargetException ex) {
          LOGGER.error("Unable to access "
                + _pojoRule.getClass().getName()
                + " when converting then to Consumer", ex);
        }
      });
    }
    return Optional.empty();
  }
}
