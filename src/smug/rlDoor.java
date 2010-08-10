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

  public Dir dir;
  public int room;

  public rlDoor(int ridx, Dir ndir, int nx, int ny)
  {
    super(Type.DOOR, new rlSymbol('+', rlColor.BROWN, rlColor.BLACK), nx, ny);
    room = ridx;
    dir = ndir;
  }
}
