package nullproblem;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.model.Auditor;
import com.deliveredtechnologies.rulebook.model.RuleStatus;
import com.deliveredtechnologies.rulebook.model.runner.RuleBookRunner;
import org.junit.Test;

import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class ShowNullProblemTest {
    @Test
    public void runWithNotNull() {
        RuleBookRunner ruleBook = new RuleBookRunner("nullproblem");
        NameValueReferableMap facts = new FactMap();
        facts.setValue("status", 1);
        facts.setValue("object", "A String");
        ruleBook.run(facts);
        Auditor auditor = (Auditor) ruleBook;
        Map<String, RuleStatus> auditMap = auditor.getRuleStatusMap();
        assertEquals(RuleStatus.EXECUTED, auditor.getRuleStatus("SimpleRule"));
        ruleBook.getResult().ifPresent(System.out::println);
    }

    @Test
    public void runWithNull() {
        RuleBookRunner ruleBook = new RuleBookRunner("nullproblem");
        NameValueReferableMap facts = new FactMap();
        facts.setValue("status", 1);
        facts.setValue("object", null);
        ruleBook.run(facts);
        Auditor auditor = (Auditor) ruleBook;
        assertEquals(RuleStatus.EXECUTED, auditor.getRuleStatus("SimpleRule"));
        ruleBook.getResult().ifPresent(System.out::println);
    }
}
