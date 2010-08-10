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
    rlMap map = new rlMap(100, 50);
    map.fillBuf(buf);
    bf.Refresh();
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
        Dimension fs = bf.getSize();
        Insets fi = win.getInsets();
        win.setSize(new Dimension(fs.width + fi.left + fi.right, fs.height + fi.top + fi.bottom));
        boolean quit = false;
        while (!quit)
        {
          if (kbd.poll(KeyEvent.VK_D))
          {
            fs = bf.getSize();
            System.out.println("bf: " + Integer.toString(fs.width) + ", " + Integer.toString(fs.height));
            fs = win.getSize();
            System.out.println("fr: " + Integer.toString(fs.width) + ", " + Integer.toString(fs.height));
            fi = win.getInsets();
            System.out.println("in: " + Integer.toString(fi.top) + ", " + Integer.toString(fi.bottom) + ", " + Integer.toString(fi.left) + ", " + Integer.toString(fi.right));
          }
          if (kbd.poll(KeyEvent.VK_Q))
            quit = true;
          if (kbd.poll(KeyEvent.VK_UP))
          {
            sy--;
            bf.scroll(sx, sy);
          }
          if (kbd.poll(KeyEvent.VK_DOWN))
          {
            sy++;
            bf.scroll(sx, sy);
          }
          if (kbd.poll(KeyEvent.VK_RIGHT))
          {
            sx++;
            bf.scroll(sx, sy);
          }
          if (kbd.poll(KeyEvent.VK_LEFT))
          {
            sx--;
            bf.scroll(sx, sy);
          }
          try
          {
            Thread.sleep(100);
          }
          catch (InterruptedException ex)
          {
          }
        }
        return null;
      }

      @Override
      protected void done()
      {
        win.setVisible(false);
        System.exit(0);
      }
    };
    loop.execute();
  }
}
