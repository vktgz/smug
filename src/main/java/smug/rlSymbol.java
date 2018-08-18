package smug;

import java.awt.Color;

public class rlSymbol
{
  final public static rlSymbol FOG = new rlSymbol(' ', rlColor.BLACK, rlColor.BLACK);

  public char code;
  public Color fgColor, bgColor;

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
