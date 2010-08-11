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
public class rlLine
{
  private ArrayList<rlPoint> buf;

  /** Creates a new instance of line */
  public rlLine(rlPoint bp, rlPoint ep)
  {
    buf = new ArrayList<rlPoint>();
    add(bp);
    double w = Math.sqrt(Math.pow((ep.col - bp.col), 2) + Math.pow((ep.row - bp.row), 2));
    float d = (float)(1 / (10 * w));
    int c, r;
    for (float t = 0; t <= 1; t = t + d)
    {
      c = Math.round(((1 - t) * bp.col) + (t * ep.col));
      r = Math.round(((1 - t) * bp.row) + (t * ep.row));
      add(new rlPoint(c, r));
    }
    add(ep);
  }

  private void add(rlPoint val)
  {
    if (!buf.contains(val))
      buf.add(val);
  }

  public int length()
  {
    return buf.size();
  }

  public rlPoint get(int idx)
  {
    rlPoint tmp = null;
    if ((idx >= 0) && (idx < buf.size()))
      tmp = buf.get(idx);
    return tmp;
  }
}