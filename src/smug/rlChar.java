/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smug;

/**
 *
 * @author vktgz
 */
public class rlChar
  extends rlObj
{
  public enum Kind
  {
    PC, NPC, PET, MON
  }

  public Kind kind;

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
    //
  }
}
