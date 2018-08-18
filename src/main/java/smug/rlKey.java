package smug;

public class rlKey
{
  public static final int RL_DBG = -7;
  public static final int RL_NONE = -1;
  public static final int RL_QUIT = 0;
  public static final int RL_MSW = 1;
  public static final int RL_MS = 2;
  public static final int RL_MSE = 3;
  public static final int RL_MW = 4;
  public static final int RL_WAIT = 5;
  public static final int RL_ME = 6;
  public static final int RL_MNW = 7;
  public static final int RL_MN = 8;
  public static final int RL_MNE = 9;
  public static final int RL_MU = 10;
  public static final int RL_MD = 11;
  public static final int RL_FONT = 12;
  public static final int RL_ABORT = 13;
  public static final int RL_OPEN = 14;
  public static final int RL_CLOSE = 15;
  public static final int RL_PICK = 16;
  public static final int RL_DROP = 17;
  public static final int RL_SSW = 21;
  public static final int RL_SS = 22;
  public static final int RL_SSE = 23;
  public static final int RL_SW = 24;
  public static final int RL_SE = 26;
  public static final int RL_SNW = 27;
  public static final int RL_SN = 28;
  public static final int RL_SNE = 29;

  public int action;
  private int key;
  private boolean shift, ctrl, alt;

  public rlKey(int naction, int nkey, boolean nshift, boolean nctrl, boolean nalt)
  {
    action = naction;
    key = nkey;
    shift = nshift;
    ctrl = nctrl;
    alt = nalt;
  }

  public boolean poll(rlKeyboard kbd, boolean cshift, boolean cctrl, boolean calt)
  {
    boolean res = (kbd.poll(key) && (shift == cshift) && (ctrl == cctrl) && (alt == calt));
    if (res)
    {
      kbd.rebound(key);
    }
    return res;
  }
}
