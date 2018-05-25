package com.deliveredtechnologies.rulebook.spring.rulebook.erroronfailure;

import com.deliveredtechnologies.rulebook.Fact;
import com.deliveredtechnologies.rulebook.annotation.*;
import org.springframework.stereotype.Component;

import static com.deliveredtechnologies.rulebook.model.RuleChainActionType.ERROR_ON_FAILURE;

@Component
@Rule(order = 1, ruleChainAction = ERROR_ON_FAILURE)
public class ErrorOnFailureTestRule {
    @Given("doThrowError")
    private Fact<Boolean> _switch;

    @Result
    private String result;

    @When
    public boolean when() {
        return true;
    }

    @Then
    public void then() throws IllegalStateException {
        if (_switch.getValue()) {
            result = "Failed";
            throw new IllegalStateException("Custom error");
        }
        result = "Passed";
    }
}

