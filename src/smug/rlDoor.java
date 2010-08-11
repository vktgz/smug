/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smug;

/**
 *
 * @author vktgz
 */
public class rlDoor
  extends rlObj
{
  public enum Dir
  {
    N, S, W, E
  }

  public enum Kind
  {
    ROOM, TUNN
  }

  public Dir dir;
  public Kind kind;

  public rlDoor(Dir ndir, Kind nkind, int nx, int ny)
  {
    super(Type.DOOR, new rlSymbol('+', rlColor.BROWN, rlColor.BLACK), nx, ny);
    dir = ndir;
    kind = nkind;
  }
}
