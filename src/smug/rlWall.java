/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smug;

/**
 *
 * @author vktgz
 */
public class rlWall
  extends rlObj
{
  public enum Kind
  {
    ROCK, WALL, CORN, SIDE
  }

  public Kind kind;

  public rlWall(Kind nkind)
  {
    super(Type.WALL, new rlSymbol('#', rlColor.GRAY, rlColor.BLACK), -1, -1);
    kind = nkind;
  }

  @Override
  public rlSymbol getSymbol()
  {
    rlSymbol s = super.getSymbol();
    if (kind == Kind.SIDE)
      s.fgColor = rlColor.YELLOW;
    if (kind == Kind.ROCK)
      s.fgColor = rlColor.DGRAY;
    if (kind == Kind.CORN)
      s.fgColor = rlColor.RED;
    return s;
  }
}
