package com.scoreapp.cli;
import com.scoreapp.model.*; import com.scoreapp.repository.*; import com.scoreapp.service.*;
import java.time.LocalDate; import java.util.*;
public class MainCLI {
  public static void main(String[] args){
    UserRepository repo = new InMemoryUserRepository();
    UserService users = new UserService(repo);
    AccountService accounts = new AccountService();
    ScoreCalculator calc = new WeightedScoreCalculator(Map.of("utilization",0.4,"punctuality",0.3,"age",0.2,"mix",0.1));
    ScoreService scoring = new ScoreService(calc);
    AdvisorService advisorSvc = new AdvisorService(List.of(new TipAdvisor(), new RiskAdvisor(), new GoalAdvisor()));
    User u = users.createUser("Juan Perez", "juan@example.com", 3000000);
    CreditCardAccount cc = accounts.addCreditCard(u, "Visa BancoX", 2000000, 0.36, LocalDate.now().minusMonths(18));
    accounts.addPayment(cc, LocalDate.now().minusMonths(1), 500000, true);
    cc.setBalance(1000000);
    ScoreResult sr = scoring.compute(u);
    System.out.println("Score: " + sr.getScore());
    System.out.println("Factores: " + sr.getFactorBreakdown());
    System.out.println("Consejos: " + advisorSvc.aggregateAdvice(u, sr));
  }
}
