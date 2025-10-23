package com.scoreapp.model;
import java.util.*; import com.scoreapp.util.Validation;
public class User {
  private final String id; private String fullName; private String email; private double monthlyIncome;
  private final List<CreditAccount> accounts = new ArrayList<>();
  public User(String fullName,String email,double monthlyIncome){
    Validation.requireNonBlank(fullName,"nombre"); Validation.requireEmail(email); Validation.requirePositive(monthlyIncome,"ingreso mensual");
    this.id=UUID.randomUUID().toString(); this.fullName=fullName; this.email=email; this.monthlyIncome=monthlyIncome;
  }
  public void addAccount(CreditAccount acc){ accounts.add(acc); }
  public String getId(){return id;} public String getFullName(){return fullName;} public String getEmail(){return email;}
  public double getMonthlyIncome(){return monthlyIncome;}
  public List<CreditAccount> getAccounts(){ return Collections.unmodifiableList(accounts); }
  public void setFullName(String fn){ Validation.requireNonBlank(fn,"nombre"); this.fullName=fn; }
  public void setMonthlyIncome(double inc){ Validation.requirePositive(inc,"ingreso mensual"); this.monthlyIncome=inc; }
}
