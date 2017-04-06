package com.deliveredtechnologies.rulebook.lang;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableTypeConvertibleMap;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.rulechain.cor.CoRRuleBook;
import org.junit.Assert;
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

  @Test
  public void ruleBookBuilderShouldBuildRulesWithaResult() {
    Consumer<NameValueReferableTypeConvertibleMap<String>> consumer = Mockito.mock(Consumer.class);
    RuleBook<Boolean> ruleBook = RuleBookBuilder.create().withResultType(Boolean.class).withDefaultResult(false)
            .addRule(rule -> rule
                    .withFactType(String.class)
                    .then((facts, result) -> result.setValue(true))
                    .then(consumer))
            .build();
    ruleBook.run(new FactMap());

    Mockito.verify(consumer, Mockito.times(1)).accept(any(NameValueReferableTypeConvertibleMap.class));
    Assert.assertTrue(ruleBook.getResult().get().getValue());
  }

  @Test
  public void ruleBookBuilderShouldCreateSpecifiedType() {
    Assert.assertNotNull(RuleBookBuilder.create(CoRRuleBook.class).build());
    Assert.assertNotNull(RuleBookBuilder.create(SampleRuleBook.class).build());
    Assert.assertNotNull(RuleBookBuilder.create(SampleRuleBook1.class).build());
    Assert.assertNull(RuleBookBuilder.create(SampleRuleBook2.class).build());
    Assert.assertNotNull(RuleBookBuilder.create(SampleRuleBook3.class).build());
  }
}
