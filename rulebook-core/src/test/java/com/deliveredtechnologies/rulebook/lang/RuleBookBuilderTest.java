package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableTypeConvertibleMap;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.function.Consumer;

import static org.mockito.Matchers.any;

/**
 * Created by clong on 4/5/17.
 */
public class RuleBookBuilderTest {
  @Test
  public void ruleBookBuilderShouldBuildRulesWithoutaResult() {
    Consumer<NameValueReferableTypeConvertibleMap<String>> consumer = Mockito.mock(Consumer.class);
    RuleBook ruleBook = RuleBookBuilder.create()
        .addRule(rule -> rule
            .withFactType(String.class)
            .then(consumer))
        .build();
    ruleBook.run(new FactMap());

    Mockito.verify(consumer, Mockito.times(1)).accept(any(NameValueReferableTypeConvertibleMap.class));
  }
}
