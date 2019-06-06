package smug;

import java.util.ArrayList;
import java.util.HashMap;

public class rlCircle
{
  private static HashMap cbuf = null;
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
      for (int ar = 1; ar <= r; ar++)
      {
        al = l * rd;
        x = Math.round(ar * (float)Math.cos(al));
        y = Math.round(ar * (float)Math.sin(al));
        e = new rlPoint(c.col + x, c.row + y);
        add(new rlLine(c, e));
      }
    }
  }

  private void add(rlLine val)
  {
    if (!lbuf.contains(val))
    {
      lbuf.add(val);
    }
    rlPoint p;
    for (int i = 0; i < val.length(); i++)
    {
      p = val.get(i);
      if (!pbuf.contains(p))
      {
        pbuf.add(p);
      }
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
    {
      tmp = pbuf.get(idx);
    }
    return tmp;
  }

  public rlLine getLine(int idx)
  {
    rlLine tmp = null;
    if ((idx >= 0) && (idx < lbuf.size()))
    {
      tmp = lbuf.get(idx);
    }
    return tmp;
  }

  public static rlCircle circle(int r)
  {
    rlCircle c = null;
    if (cbuf == null)
    {
      cbuf = new HashMap();
    }
    Integer k = new Integer(r);
    if (cbuf.containsKey(k))
    {
      c = (rlCircle)cbuf.get(k);
    }
    else
    {
      c = new rlCircle(new rlPoint(0, 0), r);
      cbuf.put(k, c);
    }
    return c;
  }
}
