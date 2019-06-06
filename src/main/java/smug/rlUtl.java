package smug;

import java.util.Arrays;

public class rlUtl
{
  public static String fill(String beg, String end, char c, int len)
  {
    String fstr = "";
    if (beg.length() + end.length() < len)
    {
      char flc[] = new char[len - beg.length() - end.length()];
      Arrays.fill(flc, c);
      fstr = new String(flc);
    }
    return beg.concat(fstr).concat(end);
  }
}
