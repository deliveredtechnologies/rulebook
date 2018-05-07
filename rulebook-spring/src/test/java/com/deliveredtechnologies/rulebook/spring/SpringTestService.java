package com.deliveredtechnologies.rulebook.spring;

import org.springframework.stereotype.Component;

@Component
public class SpringTestService {
  public static final String EXPECTED_RESULT = "Springified!";

  public String getValue() {
    return EXPECTED_RESULT;
  }
}
