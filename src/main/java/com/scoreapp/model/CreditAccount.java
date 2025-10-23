package com.scoreapp.model;
import java.time.LocalDate; import java.util.*;
public abstract class CreditAccount {
  private String id, name; private double balance, annualRate; private LocalDate openedAt;
  private final List<PaymentRecord> payments = new ArrayList<>();
  protected CreditAccount(String id,String name,double balance,double annualRate,LocalDate openedAt){
    this.id=id; this.name=name; this.balance=balance; this.annualRate=annualRate; this.openedAt=openedAt;
  }
  public void addPayment(PaymentRecord pr){ payments.add(pr); }
  public double getUtilization(){ return 0.0; }
  public String getId(){return id;} public String getName(){return name;} public double getBalance(){return balance;}
  public double getAnnualRate(){return annualRate;} public LocalDate getOpenedAt(){return openedAt;}
  public List<PaymentRecord> getPayments(){return payments;} public void setBalance(double b){this.balance=b;}
}
