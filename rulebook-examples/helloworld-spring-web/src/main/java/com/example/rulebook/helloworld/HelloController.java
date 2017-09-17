package com.example.rulebook.helloworld;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.NameValueReferableMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.model.RuleBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloController {

  @Autowired
  RuleBook<String> rulebook;

  @RequestMapping("/")
  public String index() {
    NameValueReferableMap<String> facts = new FactMap<>();
    facts.setValue("hello", "Hello ");
    facts.setValue("world", "World");
    rulebook.run(facts);

    if (rulebook.getResult().isPresent()) {
      Result<String> result = rulebook.getResult().get();
      return result.getValue();
    }
    return "no result found";
  }
}
