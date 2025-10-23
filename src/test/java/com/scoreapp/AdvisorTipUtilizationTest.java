package com.scoreapp;
import com.scoreapp.model.*; import com.scoreapp.service.*; import org.junit.jupiter.api.Test; import java.time.LocalDate; import java.util.*; import static org.junit.jupiter.api.Assertions.*;
public class AdvisorTipUtilizationTest {
  @Test void high_utilization_triggers_tip(){
    User u=new User("Marcela","mar@example.com",2000000);
    CreditCardAccount cc=new CreditCardAccount("1","Visa",900000,0.3, LocalDate.now().minusMonths(12), 1000000);
    u.addAccount(cc);
    ScoreCalculator calc=new WeightedScoreCalculator(Map.of("utilization",0.4,"punctuality",0.3,"age",0.2,"mix",0.1));
    ScoreResult sr=calc.compute(u);
    Advisor tip=new TipAdvisor();
    List<String> adv=tip.advise(u, sr);
    boolean found=adv.stream().anyMatch(s->s.toLowerCase().contains("utilizacion")||s.toLowerCase().contains("utilización"));
    assertTrue(found, "Debe sugerir bajar la utilización");
  }
}
