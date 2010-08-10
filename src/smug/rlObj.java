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
public class rlObj
{
  public enum Type
  {
    WALL, FLOOR, DOOR, CHAR
  }

  public Type id;
  private rlSymbol smb;
  public ArrayList<rlSymbol> items;
  public ArrayList<rlSymbol> chars;
  public int x, y;

  public rlObj(Type nid, rlSymbol nsmb, int nx, int ny)
  {
    id = nid;
    smb = nsmb;
    items = new ArrayList<rlSymbol>();
    chars = new ArrayList<rlSymbol>();
    x = nx;
    y = ny;
  }

  public rlSymbol getSymbol()
  {
    return smb;
  }

  @Override
  public boolean equals(Object o)
  {
    boolean eq = false;
    rlObj tmp;
    if (o instanceof rlObj)
    {
      tmp = (rlObj)o;
      eq = ((tmp.x == x) && (tmp.y == y) && (tmp.id == id));
    }
    return eq;
  }

  @Override
  public int hashCode()
  {
    int hash = 5;
    hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
    hash = 97 * hash + this.x;
    hash = 97 * hash + this.y;
    return hash;
  }
}
