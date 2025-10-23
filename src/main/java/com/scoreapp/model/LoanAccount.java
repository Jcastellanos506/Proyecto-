package com.scoreapp.model;
import java.time.LocalDate;
public final class LoanAccount extends CreditAccount {
  private double principal; private int termMonths;
  public LoanAccount(String id,String name,double balance,double annualRate,LocalDate openedAt,double principal,int termMonths){
    super(id,name,balance,annualRate,openedAt); this.principal=principal; this.termMonths=termMonths;
  }
  public double getPrincipal(){return principal;} public int getTermMonths(){return termMonths;}
}
