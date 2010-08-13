/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smug;
import java.awt.Color;

/**
 *
 * @author vktgz
 */
public class rlSymbol
{
  final public static rlSymbol FOG = new rlSymbol(' ', rlColor.BLACK, rlColor.BLACK);

  public char code;
  public Color fgColor, bgColor;

  /** Creates a new instance of symbol */
  public rlSymbol(char val, Color fg, Color bg)
  {
    code = val;
    fgColor = fg;
    bgColor = bg;
  }

  public String str()
  {
    return String.valueOf(code);
  }
}
