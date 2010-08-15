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
    N, S, W, E, U, D
  }

  public enum Kind
  {
    ROOM, TUNN, PASS
  }

  public Dir dir;
  public Kind kind;
  public boolean open;

  public rlDoor(Dir ndir, Kind nkind, int nx, int ny)
  {
    super(Type.DOOR, new rlSymbol('+', rlColor.BROWN, rlColor.BLACK), nx, ny);
    open = false;
    dir = ndir;
    kind = nkind;
    if (dir == Dir.U)
    {
      smb.code = '<';
      smb.fgColor = rlColor.GRAY;
      open = true;
    }
    if (dir == Dir.D)
    {
      smb.code = '>';
      smb.fgColor = rlColor.GRAY;
      open = true;
    }
  }

  @Override
  public rlSymbol getSymbol()
  {
    if (kind != Kind.PASS)
      if (open)
        smb.code = '/';
    return super.getSymbol();
  }
}
