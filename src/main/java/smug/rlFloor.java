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
    super(Type.FLOOR, new rlSymbol('.', rlColor.GRAY, rlColor.BLACK), nx, ny);
    kind = nkind;
  }
}
