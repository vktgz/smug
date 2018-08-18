package smug;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class rlKeyboard
    implements KeyListener
{
  private static final int KEYS = 256;
  private boolean[] keys = null;

  public rlKeyboard()
  {
    keys = new boolean[KEYS];
    for (int i = 0; i < KEYS; i++)
    {
      keys[i] = false;
    }
  }

  public boolean poll(int key)
  {
    return keys[key];
  }

  public synchronized void rebound(int key)
  {
    if ((key >= 0) && (key < KEYS))
    {
      keys[key] = false;
    }
  }

  @Override
  public synchronized void keyPressed(KeyEvent e)
  {
    int key = e.getKeyCode();
    if ((key >= 0) && (key < KEYS))
    {
      keys[key] = true;
    }
  }

  @Override
  public synchronized void keyReleased(KeyEvent e)
  {
    int key = e.getKeyCode();
    if ((key >= 0) && (key < KEYS))
    {
      keys[key] = false;
    }
  }

  @Override
  public void keyTyped(KeyEvent e)
  {
    // Not needed
  }
}
