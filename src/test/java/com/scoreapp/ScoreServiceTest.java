package com.scoreapp;
import com.scoreapp.model.*; import com.scoreapp.service.*; import org.junit.jupiter.api.*; import static org.junit.jupiter.api.Assertions.*; import java.time.LocalDate; import java.util.Map;
public class ScoreServiceTest {
  @Test void score_increases_with_on_time_payments(){
    User u=new User("Carlos","carlos@example.com",3000000);
    CreditCardAccount cc=new CreditCardAccount("1","Visa",1000000,0.3, LocalDate.now().minusMonths(24), 2000000);
    cc.addPayment(new PaymentRecord(LocalDate.now().minusMonths(1), 200000, true)); u.addAccount(cc);
    ScoreCalculator calc=new WeightedScoreCalculator(Map.of("utilization",0.4,"punctuality",0.3,"age",0.2,"mix",0.1));
    int score=calc.compute(u).getScore(); assertTrue(score>500);
  }
}
