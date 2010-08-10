/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smug;

/**
 *
 * @author vktgz
 */
public class rlFloor
  extends rlObj
{
  public enum Kind
  {
    ROOM, TUNN
  }

  public Kind kind;

  public rlFloor(Kind nkind, int nx, int ny)
  {
    super(Type.FLOOR, new rlSymbol('.', rlColor.GRAY, rlColor.BLACK), nx, ny);
    kind = nkind;
  }
}
