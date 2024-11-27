package com.hackathon.finservice.Entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AccountType {
  MAIN("Main"),
  INVEST("Invest");

  private final String type;

  AccountType(String type) {
    this.type = type;
  }

  @JsonValue
  public String type() {
    return type;
  }

  @JsonCreator
  public static AccountType forType(String value) {
    for (AccountType accountType : AccountType.values()) {
      if (accountType.type.equalsIgnoreCase(value)) {
        return accountType;
      }
    }
    throw new IllegalArgumentException("Invalid AccountType: " + value);
  }
}
