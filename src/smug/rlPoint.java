/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smug;

/**
 *
 * @author vktgz
 */
public class rlPoint
{
  public int col, row;

  /** Creates a new instance of point */
  public rlPoint(int nCol, int nRow)
  {
    col = nCol;
    row = nRow;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof rlPoint)
    {
      rlPoint tmp = (rlPoint)obj;
      if ((col == tmp.col) && (row == tmp.row))
        return true;
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 61 * hash + this.col;
    hash = 61 * hash + this.row;
    return hash;
  }
}
