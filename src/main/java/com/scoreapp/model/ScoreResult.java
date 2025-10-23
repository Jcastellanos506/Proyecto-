package com.scoreapp.model;
import java.util.Map;
public final class ScoreResult {
  private final int score; private final Map<String,Double> factorBreakdown;
  public ScoreResult(int s, Map<String,Double> fb){ this.score=s; this.factorBreakdown=fb; }
  public int getScore(){return score;} public Map<String,Double> getFactorBreakdown(){return factorBreakdown;}
}
