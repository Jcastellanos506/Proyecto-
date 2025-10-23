package com.scoreapp.service;
import java.util.*; import com.scoreapp.model.*;
public class TipAdvisor extends Advisor {
  @Override public List<String> advise(User u, ScoreResult s){
    List<String> tips = new ArrayList<>(); tips.add("Revisa tu presupuesto mensual y prioriza pagos puntuales.");
    if(s.getFactorBreakdown().getOrDefault("utilization",0.0) > 0.8){ tips.add("Utilizacion alta (>80%): baja el saldo bajo 30% del cupo."); }
    return tips;
  }
}
