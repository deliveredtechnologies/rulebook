package nullproblem;

import com.deliveredtechnologies.rulebook.RuleState;
import com.deliveredtechnologies.rulebook.annotation.*;

@Rule( name = "SimpleRule")
public class SimpleRule {
    @Given("status")
    private int status;
    @Given("object")
    private Object object;
    @Result
    private String result;

    @When
    public boolean when() {
        return true;
    }

    @Then
    public RuleState then() {
        result = "Rule was triggered with status=" + status + " and object=" + object;
        return  RuleState.NEXT;
    }
}
