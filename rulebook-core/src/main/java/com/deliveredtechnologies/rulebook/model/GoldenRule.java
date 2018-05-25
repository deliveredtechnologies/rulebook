package com.deliveredtechnologies.rulebook.model;

import com.deliveredtechnologies.rulebook.NameValueReferable;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.TypeConvertibleFactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableTypeConvertibleMap;
import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.util.ArrayUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.function.Consumer;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static com.deliveredtechnologies.rulebook.model.RuleChainActionType.CONTINUE_ON_FAILURE;
import static com.deliveredtechnologies.rulebook.model.RuleChainActionType.STOP_ON_FAILURE;
import static com.deliveredtechnologies.rulebook.model.RuleChainActionType.ERROR_ON_FAILURE;


/**
 * A standard implementation of {@link Rule}.
 * @param <T> the fact type
 * @param <U> the Result type
 */
public class GoldenRule<T, U> implements Rule<T, U> {

  private static Logger LOGGER = LoggerFactory.getLogger(GoldenRule.class);

  private NameValueReferableMap _facts = new FactMap();
  private Result<U> _result;
  private Predicate<NameValueReferableTypeConvertibleMap<T>> _condition;
  private List<Object> _actionChain = new ArrayList<>();
  private Map<Integer, List<String>> _factNames = new HashMap<>();
  private RuleState _ruleState = RuleState.NEXT;
  private Class<T> _factType;
  private RuleChainActionType _actionType;

  public GoldenRule(Class<T> factType) {
    this(factType, CONTINUE_ON_FAILURE);
  }

  public GoldenRule(Class<T> factType, RuleChainActionType actionType) {
    _factType = factType;
    _actionType = actionType;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void addFacts(NameValueReferable... facts) {
    Arrays.stream(facts).forEach(fact -> _facts.put(fact.getName(), fact));
  }

  @Override
  @SuppressWarnings("unchecked")
  public void addFacts(NameValueReferableMap facts) {
    _facts.putAll(facts);
  }

  @Override
  public void setFacts(NameValueReferableMap facts) {
    _facts = facts;
  }

  @Override
  public void setCondition(Predicate<NameValueReferableTypeConvertibleMap<T>> condition) {
    _condition = condition;
  }

  @Override
  public void setRuleState(RuleState ruleState) {
    _ruleState = ruleState;
  }

  @Override
  public void addAction(Consumer<NameValueReferableTypeConvertibleMap<T>> action) {
    if (!_actionChain.contains(action)) {
      _actionChain.add(action);
    }
  }

  @Override
  public void addAction(BiConsumer<NameValueReferableTypeConvertibleMap<T>, Result<U>> action) {
    if (!_actionChain.contains(action)) {
      _actionChain.add(action);
    }
  }

  @Override
  public void addFactNameFilter(String... factNames) {
    List<String> factNameList = Stream.of(factNames).collect(Collectors.toList());
    if (_factNames.containsKey((getActions()).size())) {
      List<String> existingFactNames = _factNames.get((getActions()).size());
      existingFactNames.addAll(factNameList);
    } else {
      _factNames.put((getActions()).size(), factNameList);
    }
  }

  @Override
  public NameValueReferableMap getFacts() {
    return _facts;
  }

  @Override
  public Predicate<NameValueReferableTypeConvertibleMap<T>> getCondition() {
    return _condition;
  }

  @Override
  public RuleState getRuleState() {
    return _ruleState;
  }

  @Override
  public List<Object> getActions() {
    return _actionChain;
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean invoke(NameValueReferableMap facts) {
    try {
      //only use facts of the specified type
      NameValueReferableMap<T> typeFilteredFacts =
              new FactMap<T>((Map<String, NameValueReferable<T>>) facts.keySet().stream()
              .filter((Object key) -> {
                NameValueReferable fact = ((NameValueReferable)facts.get(key));
                return fact.getValue() == null || _factType.isAssignableFrom(fact.getValue().getClass());
              })
              .collect(Collectors.toMap(
                  key -> key,
                  key -> ((NameValueReferable<T>) facts.get(key)))));

      //invoke then() action(s) if when() is true or if when() was never specified
      if (getCondition() == null || getCondition().test(new TypeConvertibleFactMap<T>(typeFilteredFacts))) {

        //iterate through the then() actions specified
        List<Object> actionList = getActions();
        for (int i = 0; i < (getActions()).size(); i++) {
          Object action = actionList.get(i);
          List<String> factNames = _factNames.get(i);

          //if using() fact names were specified for the specific then(), use only those facts specified
          NameValueReferableMap<T> usingFacts;
          if (factNames != null) {
            usingFacts = new FactMap<T>(factNames.stream()
                    .filter(typeFilteredFacts::containsKey)
                    .collect(Collectors.toMap(name -> name, name -> (NameValueReferable<T>)facts.get(name))));
          } else {
            usingFacts = typeFilteredFacts;
          }

          //setup a dummy Result just in case a Result is used
          Result result = new Result();

          //invoke the action
          Stream.of(action.getClass().getMethods())
                  .filter(method -> method.getName().equals("accept"))
                  .findFirst()
                    .ifPresent(method -> {
                      try {
                        method.setAccessible(true);
                        method.invoke(action,
                            ArrayUtils.combine(
                                    new Object[]{new TypeConvertibleFactMap<>(usingFacts)},
                                    new Object[]{getResult().orElseGet(() -> result)},
                                    method.getParameterCount()));
                        if (result.getValue() != null) {
                          _result = result;
                        }
                      } catch (Exception err) {
                        LOGGER.error("Error invoking action on " + action.getClass(), err);
                        if (_actionType.equals(ERROR_ON_FAILURE)) {
                          throw new RuleException(err);
                        }
                      }
                    });
          facts.putAll(usingFacts);
        }

        //stopping the rule chain only happens when the rule fails if _stopOnRuleFailure == true
        if (_actionType.equals(STOP_ON_FAILURE)) {
          this._ruleState = RuleState.NEXT;
        }
        return true;
      }
    } catch (Exception ex) {
      //catch errors from the 'when' condition; if it fails, just continue along unless ERROR_ON_FAILURE is set
      LOGGER.error("Error occurred when trying to evaluate rule!", ex);
      if (_actionType.equals(ERROR_ON_FAILURE)) {
        throw new RuleException(ex);
      }
    }

    return _actionType.equals(STOP_ON_FAILURE);
  }

  @Override
  public void setResult(Result<U> result) {
    _result = result;
  }

  @Override
  public Optional<Result<U>> getResult() {
    return Optional.ofNullable(_result);
  }
}
