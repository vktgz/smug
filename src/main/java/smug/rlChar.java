package smug;

import java.awt.Color;

public class rlChar
  extends rlObj
{
  public enum Kind
  {
    PC, NPC, PET, MON
  }

  public Kind kind;
  public int hp, mp;
  public int strength, inteligence, dexterity, perception, luck;

  public rlChar(Kind nkind, rlSymbol nsymb)
  {
    super(Type.CHAR, nsymb, 0, 0);
    kind = nkind;
    time = 100;
  }

  @Override
  public void resetTime()
  {
    time = 100;
  }

  void action()
  {
  }

  public int fov()
  {
    if (perception < 10)
    {
      return 2;
    }
    if (perception > 19)
    {
      return 4;
    }
    return 3;
  }

  public static rlChar makePC(Color color)
  {
    rlChar pc = new rlChar(rlChar.Kind.PC, new rlSymbol('@', color, rlColor.BLACK));
    pc.hp = 10;
    pc.mp = 1;
    pc.strength = 10;
    pc.inteligence = 10;
    pc.dexterity = 10;
    pc.perception = 10;
    pc.luck = 10;
    return pc;
  }
}
