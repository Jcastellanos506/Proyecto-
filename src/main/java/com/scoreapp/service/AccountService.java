package com.scoreapp.service;
import com.scoreapp.model.*; import com.scoreapp.util.Validation; import java.time.LocalDate; import java.util.UUID;
public class AccountService {
  public CreditCardAccount addCreditCard(User u,String name,double creditLimit,double rate,LocalDate openedAt){
    Validation.requireNonBlank(name,"nombre cuenta"); Validation.requirePositive(creditLimit,"cupo"); Validation.requireNonNegative(rate,"tasa");
    CreditCardAccount acc=new CreditCardAccount(UUID.randomUUID().toString(), name, 0.0, rate, openedAt, creditLimit); u.addAccount(acc); return acc;
  }
  public LoanAccount addLoan(User u,String name,double principal,double rate,int termMonths,LocalDate openedAt){
    Validation.requireNonBlank(name,"nombre cuenta"); Validation.requirePositive(principal,"principal"); Validation.requireNonNegative(rate,"tasa");
    if(termMonths<=0) throw new IllegalArgumentException("plazo debe ser positivo");
    LoanAccount acc=new LoanAccount(UUID.randomUUID().toString(), name, principal, rate, openedAt, principal, termMonths); u.addAccount(acc); return acc;
  }
  public void addPayment(CreditAccount acc, LocalDate date,double amount, boolean onTime){
    Validation.requirePositive(amount,"monto pago"); acc.addPayment(new PaymentRecord(date, amount, onTime)); acc.setBalance(Math.max(0.0, acc.getBalance()-amount));
  }
}
