package com.example.rulebook.megabank.Application;

import com.deliveredtechnologies.rulebook.model.RuleBook;
import com.deliveredtechnologies.rulebook.model.runner.RuleBookRunner;
import com.google.inject.AbstractModule;

public class RuleBookModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(RuleBook.class).toInstance(new RuleBookRunner("com.example.rulebook.megabank"));
  }
}
