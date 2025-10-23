package com.scoreapp.ui;
import javax.swing.*; import java.awt.*; import java.util.List;
public class ScoreChartPanel extends JPanel {
  private List<Integer> data;
  public ScoreChartPanel(List<Integer> data){ this.data=data; setPreferredSize(new Dimension(600,300)); setBackground(Color.WHITE); }
  public void setData(List<Integer> d){ this.data=d; repaint(); }
  @Override protected void paintComponent(Graphics g){
    super.paintComponent(g);
    Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    int w=getWidth(), h=getHeight(), pad=40; g2.setColor(Color.GRAY); g2.drawLine(pad,h-pad,w-pad,h-pad); g2.drawLine(pad,pad,pad,h-pad);
    if(data==null||data.isEmpty()) return; int n=data.size(), max=1000, min=0; int plotW=w-2*pad, plotH=h-2*pad;
    g2.setStroke(new BasicStroke(2f)); g2.setColor(new Color(66,135,245));
    int prevX=-1, prevY=-1;
    for(int i=0;i<n;i++){ int s=data.get(i); double nx=(i/(double)Math.max(1,n-1)); double ny=(s-min)/(double)(max-min);
      int x=pad+(int)(nx*plotW); int y=h-pad-(int)(ny*plotH); g2.fillOval(x-3,y-3,6,6); if(prevX!=-1) g2.drawLine(prevX,prevY,x,y); prevX=x; prevY=y; }
    g2.setColor(Color.DARK_GRAY); g2.drawString("Score (0-1000)", 8, 16);
  }
}
