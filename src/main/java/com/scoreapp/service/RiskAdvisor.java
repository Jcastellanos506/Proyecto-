package com.scoreapp.service;
import java.util.*; import com.scoreapp.model.*;
public class RiskAdvisor extends Advisor {
  @Override public List<String> advise(User u, ScoreResult s){
    List<String> out=new ArrayList<>(); int sc=s.getScore();
    if(sc<600) out.add("Riesgo alto: evita nuevas deudas hasta estabilizar pagos.");
    else if(sc<750) out.add("Riesgo medio: 6 meses de puntualidad para mejorar.");
    else out.add("Buen perfil: mantén puntualidad y baja utilizacion.");
    return out;
  }
}
