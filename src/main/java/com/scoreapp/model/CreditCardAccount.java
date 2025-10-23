package com.scoreapp.model;
import java.time.LocalDate;
public final class CreditCardAccount extends CreditAccount {
  private double creditLimit;
  public CreditCardAccount(String id,String name,double balance,double annualRate,LocalDate openedAt,double creditLimit){
    super(id,name,balance,annualRate,openedAt); this.creditLimit=creditLimit;
  }
  @Override public double getUtilization(){ if(creditLimit<=0) return 0.0; return Math.min(1.0, getBalance()/creditLimit); }
  public double getCreditLimit(){return creditLimit;} public void setCreditLimit(double l){ this.creditLimit=l; }
}
