package com.scoreapp;
import com.scoreapp.model.*; import org.junit.jupiter.api.*; import static org.junit.jupiter.api.Assertions.*; import java.time.LocalDate;
public class UtilizationTest {
  @Test void creditCard_utilization_50(){ CreditCardAccount cc=new CreditCardAccount("1","Visa",1000000,0.3, LocalDate.now().minusMonths(12), 2000000); assertEquals(0.5, cc.getUtilization(), 1e-6); }
}
