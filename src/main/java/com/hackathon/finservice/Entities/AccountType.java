package com.hackathon.finservice.Entities;

public enum AccountType {
  MAIN("Main"),
  INVEST("Invest");

  private final String type;

  AccountType(String type) {
    this.type = type;
  }

  public String type() {
    return type;
  }
}
