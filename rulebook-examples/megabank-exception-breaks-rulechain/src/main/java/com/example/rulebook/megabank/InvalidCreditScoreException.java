package com.example.rulebook.megabank;

public class InvalidCreditScoreException extends Exception {
  public InvalidCreditScoreException(String message) {
    super(message);
  }
}
