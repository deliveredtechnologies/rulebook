package com.deliveredtechnologies.rulebook;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

/**
 * Created by clong on 2/6/17.
 * Tests for {@link RuleBook}
 */
public class RuleBookTest {
    @Test
    public void ruleBooksRunRules() {
        @SuppressWarnings("unchecked")
        Rule<String> rule = (Rule<String>)mock(Rule.class);
        Fact<String> fact = new Fact<String>("hello", "world");
        RuleBook<String> ruleBook = spy(new RuleBook<String>() {
            @Override
            protected void defineRules() { }
        });

        ruleBook.given(fact).addRule(rule);
        ruleBook.run();

        verify(rule, times(1)).given(anyList());
        verify(ruleBook, times(1)).defineRules();
        verify(rule, times(1)).run();
    }
}
