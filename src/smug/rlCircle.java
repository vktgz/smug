/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smug;

import java.util.ArrayList;

/**
 *
 * @author vktgz
 */
public class rlCircle
{
  private ArrayList<rlPoint> pbuf;
  private ArrayList<rlLine> lbuf;

  public rlCircle(rlPoint c, int r)
  {
    pbuf = new ArrayList<rlPoint>();
    lbuf = new ArrayList<rlLine>();
    float al;
    int x, y;
    rlPoint e;
    float rd = (float)(Math.PI / 180);
    for (int l = 0; l < 360; l++)
    {
      al = l * rd;
      x = Math.round(r * (float)Math.cos(al));
      y = Math.round(r * (float)Math.sin(al));
      e = new rlPoint(c.col + x, c.row + y);
      add(new rlLine(c, e));
    }
  }

  private void add(rlLine val)
  {
    if (!lbuf.contains(val))
      lbuf.add(val);
    rlPoint p;
    for (int i = 0; i < val.length(); i++)
    {
      p = val.get(i);
      if (!pbuf.contains(p))
        pbuf.add(p);
    }
  }

  public int points()
  {
    return pbuf.size();
  }

  public int lines()
  {
    return lbuf.size();
  }

  public rlPoint getPoint(int idx)
  {
    rlPoint tmp = null;
    if ((idx >= 0) && (idx < pbuf.size()))
      tmp = pbuf.get(idx);
    return tmp;
  }

  public rlLine getLine(int idx)
  {
    rlLine tmp = null;
    if ((idx >= 0) && (idx < lbuf.size()))
      tmp = lbuf.get(idx);
    return tmp;
  }
}
