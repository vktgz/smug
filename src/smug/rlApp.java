/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smug;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author vktgz
 */
public class rlApp
{
  private JFrame win;
  private rlBattlefield bf;
  private int sx, sy;
  private rlKeyboard kbd;
  private rlMap map;
  private rlChar pc;
  private long time;

  public rlApp()
  {
    sx = 0;
    sy = 0;
    win = new JFrame("smug");
    win.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    win.getContentPane().setLayout(null);
    bf = new rlBattlefield();
    bf.setCols(80);
    bf.setRows(25);
    rlBuffer buf = new rlBuffer(100, 50, new rlSymbol(' ', rlColor.GRAY, rlColor.BLACK));
    bf.setBuffer(buf);
    bf.setFont(new java.awt.Font("Monospaced", 1, 18));
    win.getContentPane().add(bf);
    win.setPreferredSize(bf.getPreferredSize());
    win.setResizable(false);
    win.pack();
    kbd = new rlKeyboard();
    win.addKeyListener(kbd);
    bf.addKeyListener(kbd);
}

  public void run()
  {
    win.setVisible(true);
    javax.swing.SwingWorker loop = new javax.swing.SwingWorker<Void, Void>()
    {
      @Override
      protected Void doInBackground()
      {
        initGame();
        boolean quit = false;
        rlObj o;
        while (!quit)
        {
          Redraw();
          time++;
          for (int t = 0; t < map.timer.size(); t++)
          {
            o = map.timer.get(t);
            if (o instanceof rlChar)
            {
              rlChar c = (rlChar)o;
              c.time--;
              if (c.time == 0)
              {
                if (c.kind == rlChar.Kind.PC)
                  quit = getInput();
                else
                  c.action();
                c.resetTime();
              }
            }
          }
        }
        return null;
      }

      @Override
      protected void done()
      {
        win.setVisible(false);
        System.out.println("Terminated.");
        System.exit(0);
      }
    };
    loop.execute();
  }

  private void initGame()
  {
    Dimension fs = bf.getSize();
    Insets fi = win.getInsets();
    win.setSize(new Dimension(fs.width + fi.left + fi.right, fs.height + fi.top + fi.bottom));
    map = new rlMap(100, 50);
    map.generateMap();
    pc = new rlChar(rlChar.Kind.PC, new rlSymbol('@', rlColor.DGRAY, rlColor.BLACK));
    map.stUp.chars.add(pc);
    map.timer.add(pc);
    pc.x = map.stUp.x;
    pc.y = map.stUp.y;
    time = 0;
  }

  private void Redraw()
  {
    map.setVisible(pc.x, pc.y, 3);
    map.fillBuf(bf.getBuffer());
    bf.Refresh();
    bf.scroll(pc.x - 40 + sx, pc.y - 12 + sy);
  }

  private boolean getInput()
  {
    boolean quit = false;
    boolean key = false;
    boolean shift, ctrl, alt;
    while (!key)
    {
      shift = kbd.poll(KeyEvent.VK_SHIFT);
      ctrl = kbd.poll(KeyEvent.VK_CONTROL);
      alt = kbd.poll(KeyEvent.VK_ALT);
      if (kbd.poll(KeyEvent.VK_D))
      {
        key = true;
/*        fs = bf.getSize();
        System.out.println("bf: " + Integer.toString(fs.width) + ", " + Integer.toString(fs.height));
        fs = win.getSize();
        System.out.println("fr: " + Integer.toString(fs.width) + ", " + Integer.toString(fs.height));
        fi = win.getInsets();
        System.out.println("in: " + Integer.toString(fi.top) + ", " + Integer.toString(fi.bottom) + ", " + Integer.toString(fi.left) + ", " + Integer.toString(fi.right)); */
        kbd.rebound(KeyEvent.VK_D);
      }
      if (kbd.poll(KeyEvent.VK_Q))
      {
        key = true;
        quit = true;
        kbd.rebound(KeyEvent.VK_Q);
      }
      if (kbd.poll(KeyEvent.VK_UP))
      {
        key = true;
        if (shift)
        {
          sy--;
        }
        else
          movePC(pc.x, pc.y - 1);
        kbd.rebound(KeyEvent.VK_UP);
      }
      if (kbd.poll(KeyEvent.VK_DOWN))
      {
        key = true;
        if (shift)
        {
          sy++;
        }
        else
          movePC(pc.x, pc.y + 1);
        kbd.rebound(KeyEvent.VK_DOWN);
      }
      if (kbd.poll(KeyEvent.VK_RIGHT))
      {
        key = true;
        if (shift)
        {
          sx++;
          if (ctrl)
            sy++;
          if (alt)
            sy--;
        }
        else
        {
          if (ctrl)
            movePC(pc.x + 1, pc.y + 1);
          else if (alt)
            movePC(pc.x + 1, pc.y - 1);
          else
            movePC(pc.x + 1, pc.y);
        }
        kbd.rebound(KeyEvent.VK_RIGHT);
      }
      if (kbd.poll(KeyEvent.VK_LEFT))
      {
        key = true;
        if (shift)
        {
          sx--;
          if (ctrl)
            sy++;
          if (alt)
            sy--;
        }
        else
        {
          if (ctrl)
            movePC(pc.x - 1, pc.y + 1);
          else if (alt)
            movePC(pc.x - 1, pc.y - 1);
          else
            movePC(pc.x - 1, pc.y);
        }
        kbd.rebound(KeyEvent.VK_LEFT);
      }
      if (!key)
      {
        try
        {
          Thread.sleep(100);
        }
        catch (InterruptedException ex)
        {
        }
      }
    }
    return quit;
  }

  private void movePC(int tx, int ty)
  {
    rlObj no = map.map.get(ty).get(tx);
    if ((no instanceof rlFloor) || (no instanceof rlDoor))
    {
      if (no.chars.isEmpty())
      {
        rlObj co = map.map.get(pc.y).get(pc.x);
        co.chars.remove(pc);
        no.chars.add(pc);
        pc.x = tx;
        pc.y = ty;
      }
    }
    else
      addMsg("Can't move there");
  }

  private void addMsg(String msg)
  {
    //
  }
}
