package com.scoreapp.ui;
import com.scoreapp.repository.*; import com.scoreapp.service.*; import javax.swing.*; import java.util.List; import java.util.Map;
public class MainSwing {
  public static void main(String[] args){
    SwingUtilities.invokeLater(() -> {
      UserRepository repo = new InMemoryUserRepository();
      UserService userSvc = new UserService(repo);
      AccountService accountSvc = new AccountService();
      ScoreCalculator calc = new WeightedScoreCalculator(Map.of("utilization",0.4,"punctuality",0.3,"age",0.2,"mix",0.1));
      ScoreService scoreSvc = new ScoreService(calc);
      AdvisorService advisorSvc = new AdvisorService(List.of(new TipAdvisor(), new RiskAdvisor(), new GoalAdvisor()));
      javax.swing.JFrame f = new MainWindow(userSvc, accountSvc, scoreSvc, advisorSvc);
      f.setVisible(true);
    });
  }
}
