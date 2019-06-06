package smug;

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
    super(Type.FLOOR, new rlSymbol('.', rlColor.DGRAY, rlColor.BLACK), nx, ny);
    kind = nkind;
  }

  @Override
  public void setVisible(boolean content, boolean val)
  {
    super.setVisible(content, val);
    if (content && val)
    {
      smb.fgColor = rlColor.GRAY;
    }
    else
    {
      smb.fgColor = rlColor.DGRAY;
    }
  }
}
