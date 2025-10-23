package com.scoreapp.service;
import com.scoreapp.model.*; import java.time.*; import java.time.temporal.ChronoUnit; import java.util.*;
public class WeightedScoreCalculator implements ScoreCalculator {
  private final Map<String,Double> weights;
  public WeightedScoreCalculator(Map<String,Double> w){ this.weights=new HashMap<>(w); }
  @Override public ScoreResult compute(User u){
    double utilization = avgUtil(u); double punctuality = punctuality(u); double age = avgAge(u); double mix = mix(u);
    double score=1000.0;
    score -= weights.getOrDefault("utilization",0.4)*utilization*600;
    score -= weights.getOrDefault("punctuality",0.3)*(1.0-punctuality)*500;
    score -= weights.getOrDefault("age",0.2)*(1.0-age)*200;
    score += weights.getOrDefault("mix",0.1)*mix*100;
    int finalScore=(int)Math.max(0, Math.min(1000, Math.round(score)));
    Map<String,Double> fb=new LinkedHashMap<>(); fb.put("utilization",utilization); fb.put("punctuality",punctuality); fb.put("age",age); fb.put("mix",mix);
    return new ScoreResult(finalScore, fb);
  }
  private double avgUtil(User u){ return u.getAccounts().stream().mapToDouble(CreditAccount::getUtilization).average().orElse(0.0); }
  private double punctuality(User u){ long on=0, tot=0; for(CreditAccount a:u.getAccounts()){ for(PaymentRecord p:a.getPayments()){ tot++; if(p.isOnTime()) on++; } } return tot==0?1.0:on/(double)tot; }
  private double avgAge(User u){ double m=u.getAccounts().stream().mapToLong(a->ChronoUnit.MONTHS.between(a.getOpenedAt(), LocalDate.now())).average().orElse(0.0); return Math.min(1.0, m/60.0); }
  private double mix(User u){ boolean c=u.getAccounts().stream().anyMatch(a->a instanceof CreditCardAccount); boolean l=u.getAccounts().stream().anyMatch(a->a instanceof LoanAccount); int t=(c?1:0)+(l?1:0); return t/2.0; }
}
