package com.scoreapp.model;
import java.time.LocalDate;
public class PaymentRecord {
  private LocalDate date; private double amount; private boolean onTime;
  public PaymentRecord(LocalDate d, double a, boolean t){ this.date=d; this.amount=a; this.onTime=t; }
  public LocalDate getDate(){return date;} public double getAmount(){return amount;} public boolean isOnTime(){return onTime;}
}
