package smug;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class rlApp
{
  final private int MSGS = 1000;

  private JFrame win;
  private rlBattlefield bf;
  private int sx, sy, nmsg;
  private rlKeyboard kbd;
  private rlMap map;
  private rlChar pc;
  private long time;
  private LinkedList<String> mbuf;
  private HashMap maps, keyset;

  public rlApp()
  {
    mbuf = new LinkedList<String>();
    maps = new HashMap();
    keyset = new HashMap();
    sx = 0;
    sy = 0;
    nmsg = 0;
    win = new JFrame("smug");
    win.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    win.getContentPane().setLayout(null);
    win.setIconImage(new ImageIcon(rlApp.class.getResource("res/smug.png")).getImage());
    InputStream is = rlApp.class.getResourceAsStream("res/DejaVuSansMono.ttf");
    try
    {
      Font fb = Font.createFont(Font.TRUETYPE_FONT, is);
      GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fb);
    }
    catch (Exception ex)
    {
    }
    bf = new rlBattlefield();
    bf.setCols(80);
    bf.setRows(25);
    rlBuffer buf = new rlBuffer(320, 100, new rlSymbol(' ', rlColor.GRAY, rlColor.BLACK));
    bf.setBuffer(buf);
    bf.setFont(new Font("DejaVu Sans Mono", 1, 14));
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
        loadCfg();
        initGame();
        boolean quit = false, draw = true;
        rlObj o;
        while (!quit)
        {
          if (draw)
          {
            Redraw();
          }
          draw = false;
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
                {
                  quit = getInput();
                }
                else
                {
                  c.action();
                }
                c.resetTime();
                draw = true;
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
        saveCfg();
        System.out.println("Terminated.");
        System.exit(0);
      }
    };
    loop.execute();
  }

  private void initGame()
  {
    try
    {
      Thread.sleep(100);
      fixWindow();
      Thread.sleep(100);
      win.setLocationRelativeTo(null);
      Thread.sleep(100);
    }
    catch (InterruptedException ex)
    {
    }
    makeKeyset();
    map = new rlMap(32, 16, "CTY", 0);
    map.generateSpecialMap("CTY");
    pc = rlChar.makePC(rlColor.DGRAY);
    map.map.get(2).get(2).chars.add(pc);
    map.timer.add(pc);
    pc.x = 2;
    pc.y = 2;
    time = 0;
    maps.put(map.getID(), map);
  }

  private void fixWindow()
  {
    Dimension fs = bf.getSize();
    Insets fi = win.getInsets();
    win.setSize(new Dimension(fs.width + fi.left + fi.right, fs.height + fi.top + fi.bottom));
  }

  private void Redraw()
  {
    map.setVisible(pc.x, pc.y, pc.fov());
    bf.getBuffer().clear();
    map.fillBuf(bf.getBuffer());
    bf.scroll(pc.x - 40 + sx, pc.y - 12 + sy);
    bf.cleanOSD();
    drawMsg();
    drawStatus();
    bf.Refresh();
  }

  private int wait4key(ArrayList<rlKey> keys)
  {
    int res = rlKey.RL_NONE;
    boolean shift, ctrl, alt;
    boolean done = false;
    while (!done)
    {
      shift = kbd.poll(KeyEvent.VK_SHIFT);
      ctrl = kbd.poll(KeyEvent.VK_CONTROL);
      alt = kbd.poll(KeyEvent.VK_ALT);
      for (int i = 0; i < keys.size(); i++)
      {
        rlKey key = keys.get(i);
        if (key.poll(kbd, shift, ctrl, alt))
        {
          done = true;
          res = key.action;
          break;
        }
      }
      if (!done)
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
    return res;
  }

  private boolean getInput()
  {
    boolean quit = false;
    int key = wait4key((ArrayList<rlKey>)keyset.get("main"));
    if (key == rlKey.RL_DBG)
    {
    }
    if (key == rlKey.RL_QUIT)
    {
      quit = true;
    }
    if (key == rlKey.RL_MN)
    {
      movePC(pc.x, pc.y - 1);
    }
    if (key == rlKey.RL_MS)
    {
      movePC(pc.x, pc.y + 1);
    }
    if (key == rlKey.RL_MW)
    {
      movePC(pc.x - 1, pc.y);
    }
    if (key == rlKey.RL_ME)
    {
      movePC(pc.x + 1, pc.y);
    }
    if (key == rlKey.RL_MNW)
    {
      movePC(pc.x - 1, pc.y - 1);
    }
    if (key == rlKey.RL_MNE)
    {
      movePC(pc.x + 1, pc.y - 1);
    }
    if (key == rlKey.RL_MSW)
    {
      movePC(pc.x - 1, pc.y + 1);
    }
    if (key == rlKey.RL_MSE)
    {
      movePC(pc.x + 1, pc.y + 1);
    }
    if (key == rlKey.RL_MU)
    {
      boolean mvd = false;
      rlObj o = map.map.get(pc.y).get(pc.x);
      if (o instanceof rlDoor)
      {
        rlDoor p = (rlDoor)o;
        if (p.dir == rlDoor.Dir.U)
        {
          moveMap(p);
          mvd = true;
        }
      }
      if (!mvd)
      {
        addMsg("There are no stairs to ascend.");
      }
    }
    if (key == rlKey.RL_MD)
    {
      boolean mvd = false;
      rlObj o = map.map.get(pc.y).get(pc.x);
      if (o instanceof rlDoor)
      {
        rlDoor p = (rlDoor)o;
        if (p.dir == rlDoor.Dir.D)
        {
          moveMap(p);
          mvd = true;
        }
      }
      if (!mvd)
      {
        addMsg("There are no stairs to descend.");
      }
    }
    if (key == rlKey.RL_SN)
    {
      sy--;
    }
    if (key == rlKey.RL_SS)
    {
      sy++;
    }
    if (key == rlKey.RL_SW)
    {
      sx--;
    }
    if (key == rlKey.RL_SE)
    {
      sx++;
    }
    if (key == rlKey.RL_SNW)
    {
      sx--;
      sy--;
    }
    if (key == rlKey.RL_SNE)
    {
      sx++;
      sy--;
    }
    if (key == rlKey.RL_SSW)
    {
      sx--;
      sy++;
    }
    if (key == rlKey.RL_SSE)
    {
      sx++;
      sy++;
    }
    if (key == rlKey.RL_OPEN)
    {
      openDoor(true);
    }
    if (key == rlKey.RL_CLOSE)
    {
      openDoor(false);
    }
    if (key == rlKey.RL_PICK)
    {
      rlObj o = map.map.get(pc.y).get(pc.x);
      if (o.items.isEmpty())
      {
        addMsg("There is nothing to pick up.");
      }
      else
      {
        pickup(o);
      }
    }
    if (key == rlKey.RL_FONT)
    {
      selectFont();
    }
    return quit;
  }

  private void movePC(int tx, int ty)
  {
    rlObj no = map.map.get(ty).get(tx);
    if ((no instanceof rlFloor) || (no instanceof rlDoor))
    {
      if (no instanceof rlDoor)
      {
        if (!((rlDoor)no).open)
        {
          addMsg("Doors are closed.");
          return;
        }
      }
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
    {
      addMsg("Can't move there.");
    }
  }

  private void addMsg(String msg)
  {
    mbuf.addLast(msg);
    if (mbuf.size() > MSGS)
    {
      mbuf.removeFirst();
    }
    nmsg++;
  }

  private void drawMsg()
  {
    String line0 = "", line1 = "", tmp = "", msg = "";
    if ((!mbuf.isEmpty()) && (nmsg > 0))
    {
      ListIterator<String> i = mbuf.listIterator(mbuf.size());
      if (nmsg > 1)
      {
        msg = i.previous();
        if (msg.length() > 80)
        {
          int d = msg.length() - 80;
          line1 = msg.substring(d);
          tmp = msg.substring(0, d);
        }
        else
        {
          line1 = msg;
        }
      }
      if (tmp.length() > 0)
      {
        if (tmp.length() > 80)
        {
          line0 = tmp.substring(tmp.length() - 80);
        }
        else
        {
          line0 = tmp;
        }
      }
      else if (i.hasPrevious())
      {
        msg = i.previous();
        if (msg.length() > 80)
        {
          line0 = msg.substring(msg.length() - 80);
        }
        else
        {
          line0 = msg;
        }
      }
      nmsg = 0;
    }
    line0 = rlUtl.fill(line0, "", ' ', 80);
    line1 = rlUtl.fill(line1, "", ' ', 80);
    bf.drawStringOSD(new rlPoint(1, 1), line0);
    bf.drawStringOSD(new rlPoint(1, 2), line1);
  }

  private void openDoor(boolean ns)
  {
    rlObj o;
    for (int x = pc.x - 1; x < pc.x + 2; x++)
    {
      for (int y = pc.y - 1; y < pc.y + 2; y++)
      {
        if ((x != pc.x) || (y != pc.y))
        {
          o = map.map.get(y).get(x);
          if (o instanceof rlDoor)
          {
            rlDoor d = (rlDoor)o;
            if ((d.kind != rlDoor.Kind.PASS) && (d.open != ns))
            {
              d.open = ns;
              return;
            }
          }
        }
      }
    }
    if (ns)
    {
      addMsg("There are no doors to open.");
    }
    else
    {
      addMsg("There are no doors to close.");
    }
  }

  private void selectFont()
  {
    rlFont dlg = new rlFont(win);
    dlg.setFont(bf.getFont());
    dlg.setLocationRelativeTo(win);
    dlg.setVisible(true);
    if (dlg.ok)
    {
      bf.setFont(dlg.getFont());
      fixWindow();
    }
  }

  private void moveMap(rlDoor st)
  {
    rlMap nmap = getMap(st);
    st.chars.remove(pc);
    rlDoor nst = (rlDoor)nmap.stairs.get(map.getID());
    nst.chars.add(pc);
    pc.x = nst.x;
    pc.y = nst.y;
    map.timer.remove(pc);
    nmap.timer.add(pc);
    map = nmap;
  }

  private void drawStatus()
  {
    String line0 = "", line0end = "", line1 = "", line1end = "";
    line0 = "Player Str: " + pc.strength
      + " Int: " + pc.inteligence
      + " Dex: " + pc.dexterity
      + " Per: " + pc.perception
      + " Lck: " + pc.luck;
    line1 = "HP: " + pc.hp + " MP: " + pc.mp;
    if (map.mLvl > 0)
    {
      line1end = "Level: " + map.getID();
    }
    else
    {
      line1end = "Level: " + map.mID;
    }
    line0 = rlUtl.fill(line0, line0end, ' ', 80);
    line1 = rlUtl.fill(line1, line1end, ' ', 80);
    bf.drawStringOSD(new rlPoint(1, 24), line0);
    bf.drawStringOSD(new rlPoint(1, 25), line1);
  }

  private rlMap getMap(rlDoor st)
  {
    rlMap nmap = (rlMap)maps.get(st.getID());
    if (nmap == null)
    {
      nmap = new rlMap(100, 50, st.dID, st.dLvl);
      if (nmap.mID.equals("TWR"))
      {
        nmap.generateRandomMap(rlDoor.Dir.U);
      }
      else
      {
        nmap.generateRandomMap(rlDoor.Dir.D);
      }
      if (st.dLvl == 1)
      {
        String fID = st.dID + Integer.toString(0);
        rlDoor nst = (rlDoor)nmap.stairs.get(fID);
        nmap.stairs.remove(fID);
        nst.setID("CTY", 0);
        nmap.stairs.put(nst.getID(), nst);
      }
      if (!nmap.mID.equals("RDM"))
      {
        maps.put(nmap.getID(), nmap);
      }
    }
    return nmap;
  }

  private void pickup(rlObj o)
  {
    while (!o.items.isEmpty())
    {
      rlItem i = (rlItem)o.items.remove(0);
      addMsg("You pick up ".concat(i.getName()).concat("."));
      pc.items.add(i);
    }
  }

  private void makeKeyset()
  {
    ArrayList<rlKey> keys = new ArrayList<rlKey>();
    keys.add(new rlKey(rlKey.RL_DBG, KeyEvent.VK_D, false, false, true));
    keys.add(new rlKey(rlKey.RL_QUIT, KeyEvent.VK_Q, false, true, false));
    keys.add(new rlKey(rlKey.RL_FONT, KeyEvent.VK_F, false, true, false));
    keys.add(new rlKey(rlKey.RL_MN, KeyEvent.VK_UP, false, false, false));
    keys.add(new rlKey(rlKey.RL_MS, KeyEvent.VK_DOWN, false, false, false));
    keys.add(new rlKey(rlKey.RL_MW, KeyEvent.VK_LEFT, false, false, false));
    keys.add(new rlKey(rlKey.RL_ME, KeyEvent.VK_RIGHT, false, false, false));
    keys.add(new rlKey(rlKey.RL_MN, KeyEvent.VK_NUMPAD8, false, false, false));
    keys.add(new rlKey(rlKey.RL_MS, KeyEvent.VK_NUMPAD2, false, false, false));
    keys.add(new rlKey(rlKey.RL_MW, KeyEvent.VK_NUMPAD4, false, false, false));
    keys.add(new rlKey(rlKey.RL_ME, KeyEvent.VK_NUMPAD6, false, false, false));
    keys.add(new rlKey(rlKey.RL_MNW, KeyEvent.VK_NUMPAD7, false, false, false));
    keys.add(new rlKey(rlKey.RL_MNE, KeyEvent.VK_NUMPAD9, false, false, false));
    keys.add(new rlKey(rlKey.RL_MSW, KeyEvent.VK_NUMPAD1, false, false, false));
    keys.add(new rlKey(rlKey.RL_MSE, KeyEvent.VK_NUMPAD3, false, false, false));
    keys.add(new rlKey(rlKey.RL_MNW, KeyEvent.VK_LEFT, false, false, true));
    keys.add(new rlKey(rlKey.RL_MNE, KeyEvent.VK_RIGHT, false, false, true));
    keys.add(new rlKey(rlKey.RL_MSW, KeyEvent.VK_LEFT, false, true, false));
    keys.add(new rlKey(rlKey.RL_MSE, KeyEvent.VK_RIGHT, false, true, false));
    keys.add(new rlKey(rlKey.RL_MU, KeyEvent.VK_COMMA, true, false, false));
    keys.add(new rlKey(rlKey.RL_MD, KeyEvent.VK_PERIOD, true, false, false));
    keys.add(new rlKey(rlKey.RL_OPEN, KeyEvent.VK_O, false, false, false));
    keys.add(new rlKey(rlKey.RL_CLOSE, KeyEvent.VK_C, false, false, false));
    keys.add(new rlKey(rlKey.RL_PICK, KeyEvent.VK_COMMA, false, false, false));
    keys.add(new rlKey(rlKey.RL_DROP, KeyEvent.VK_D, false, false, false));
    keys.add(new rlKey(rlKey.RL_SN, KeyEvent.VK_UP, true, false, false));
    keys.add(new rlKey(rlKey.RL_SS, KeyEvent.VK_DOWN, true, false, false));
    keys.add(new rlKey(rlKey.RL_SW, KeyEvent.VK_LEFT, true, false, false));
    keys.add(new rlKey(rlKey.RL_SE, KeyEvent.VK_RIGHT, true, false, false));
    keys.add(new rlKey(rlKey.RL_SN, KeyEvent.VK_NUMPAD8, true, false, false));
    keys.add(new rlKey(rlKey.RL_SS, KeyEvent.VK_NUMPAD2, true, false, false));
    keys.add(new rlKey(rlKey.RL_SW, KeyEvent.VK_NUMPAD4, true, false, false));
    keys.add(new rlKey(rlKey.RL_SE, KeyEvent.VK_NUMPAD6, true, false, false));
    keys.add(new rlKey(rlKey.RL_SNW, KeyEvent.VK_NUMPAD7, true, false, false));
    keys.add(new rlKey(rlKey.RL_SNE, KeyEvent.VK_NUMPAD9, true, false, false));
    keys.add(new rlKey(rlKey.RL_SSW, KeyEvent.VK_NUMPAD1, true, false, false));
    keys.add(new rlKey(rlKey.RL_SSE, KeyEvent.VK_NUMPAD3, true, false, false));
    keys.add(new rlKey(rlKey.RL_SN, KeyEvent.VK_KP_UP, true, false, false));
    keys.add(new rlKey(rlKey.RL_SS, KeyEvent.VK_KP_DOWN, true, false, false));
    keys.add(new rlKey(rlKey.RL_SW, KeyEvent.VK_KP_LEFT, true, false, false));
    keys.add(new rlKey(rlKey.RL_SE, KeyEvent.VK_KP_RIGHT, true, false, false));
    keys.add(new rlKey(rlKey.RL_SNW, KeyEvent.VK_HOME, true, false, false));
    keys.add(new rlKey(rlKey.RL_SNE, KeyEvent.VK_PAGE_UP, true, false, false));
    keys.add(new rlKey(rlKey.RL_SSW, KeyEvent.VK_END, true, false, false));
    keys.add(new rlKey(rlKey.RL_SSE, KeyEvent.VK_PAGE_DOWN, true, false, false));
    keyset.put("main", keys);
  }

  private void loadCfg()
  {
    Preferences cfg = Preferences.userRoot().node("smug");
    bf.setFont(new Font(cfg.get("font_name", "DejaVu Sans Mono"), 1, cfg.getInt("font_size", 14)));
  }

  private void saveCfg()
  {
    Preferences cfg = Preferences.userRoot().node("smug");
    cfg.put("font_name", bf.getFont().getFamily());
    cfg.putInt("font_size", bf.getFont().getSize());
  }
}
