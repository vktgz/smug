/*
 * smug
 * rlApp.java
 * Copyright (C) vktgz 2010 <vktgz@jabster.pl>
 * License: GPLv3
 */

package smug;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import javax.jnlp.BasicService;
import javax.jnlp.FileContents;
import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class rlApp
{
	final private int MSGS = 1000;
	//
	private JFrame win;
	private rlBattlefield bf;
	private int sx, sy, nmsg;
	private rlKeyboard kbd;
	private rlMap map;
	private rlChar pc;
	private long time;
	private LinkedList<String> mbuf;
	private BasicService bs;
	private PersistenceService ps;
	private URL codebase;
	private HashMap maps;

	public rlApp()
	{
		mbuf = new LinkedList<String>();
		maps = new HashMap();
		sx = 0;
		sy = 0;
		nmsg = 0;
		win = new JFrame("smug");
		win.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		win.getContentPane().setLayout(null);
		win.setIconImage(new ImageIcon(rlApp.class.getResource("smug.png")).getImage());
		bf = new rlBattlefield();
		bf.setCols(80);
		bf.setRows(25);
		rlBuffer buf = new rlBuffer(320, 100, new rlSymbol(' ', rlColor.GRAY, rlColor.BLACK));
		bf.setBuffer(buf);
		bf.setFont(new Font("Monospaced", 1, 14));
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
				loadData();
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
				saveData();
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
		map = new rlMap(32, 16, "CTY", 0);
		map.generateSpecialMap("CTY");
		pc = new rlChar(rlChar.Kind.PC, new rlSymbol('@', rlColor.DGRAY, rlColor.BLACK));
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
		map.setVisible(pc.x, pc.y, 3);
		bf.getBuffer().clear();
		map.fillBuf(bf.getBuffer());
		bf.scroll(pc.x - 40 + sx, pc.y - 12 + sy);
		bf.cleanOSD();
		drawMsg();
		drawStatus();
		bf.Refresh();
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
				kbd.rebound(KeyEvent.VK_D);
			}
			if (kbd.poll(KeyEvent.VK_Q))
			{
				key = true;
				if (ctrl)
				{
					quit = true;
				}
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
				{
					movePC(pc.x, pc.y - 1);
				}
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
				{
					movePC(pc.x, pc.y + 1);
				}
				kbd.rebound(KeyEvent.VK_DOWN);
			}
			if (kbd.poll(KeyEvent.VK_RIGHT))
			{
				key = true;
				if (shift)
				{
					sx++;
					if (ctrl)
					{
						sy++;
					}
					if (alt)
					{
						sy--;
					}
				}
				else
				{
					if (ctrl)
					{
						movePC(pc.x + 1, pc.y + 1);
					}
					else if (alt)
					{
						movePC(pc.x + 1, pc.y - 1);
					}
					else
					{
						movePC(pc.x + 1, pc.y);
					}
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
					{
						sy++;
					}
					if (alt)
					{
						sy--;
					}
				}
				else
				{
					if (ctrl)
					{
						movePC(pc.x - 1, pc.y + 1);
					}
					else if (alt)
					{
						movePC(pc.x - 1, pc.y - 1);
					}
					else
					{
						movePC(pc.x - 1, pc.y);
					}
				}
				kbd.rebound(KeyEvent.VK_LEFT);
			}
			if (kbd.poll(KeyEvent.VK_NUMPAD8))
			{
				key = true;
				if (shift)
				{
					sy--;
				}
				else
				{
					movePC(pc.x, pc.y - 1);
				}
				kbd.rebound(KeyEvent.VK_NUMPAD8);
			}
			if (kbd.poll(KeyEvent.VK_NUMPAD2))
			{
				key = true;
				if (shift)
				{
					sy++;
				}
				else
				{
					movePC(pc.x, pc.y + 1);
				}
				kbd.rebound(KeyEvent.VK_NUMPAD2);
			}
			if (kbd.poll(KeyEvent.VK_NUMPAD6))
			{
				key = true;
				if (shift)
				{
					sx++;
				}
				else
				{
					movePC(pc.x + 1, pc.y);
				}
				kbd.rebound(KeyEvent.VK_NUMPAD6);
			}
			if (kbd.poll(KeyEvent.VK_NUMPAD9))
			{
				key = true;
				if (shift)
				{
					sx++;
					sy--;
				}
				else
				{
					movePC(pc.x + 1, pc.y - 1);
				}
				kbd.rebound(KeyEvent.VK_NUMPAD9);
			}
			if (kbd.poll(KeyEvent.VK_NUMPAD3))
			{
				key = true;
				if (shift)
				{
					sx++;
					sy++;
				}
				else
				{
					movePC(pc.x + 1, pc.y + 1);
				}
				kbd.rebound(KeyEvent.VK_NUMPAD3);
			}
			if (kbd.poll(KeyEvent.VK_NUMPAD4))
			{
				key = true;
				if (shift)
				{
					sx--;
				}
				else
				{
					movePC(pc.x - 1, pc.y);
				}
				kbd.rebound(KeyEvent.VK_NUMPAD4);
			}
			if (kbd.poll(KeyEvent.VK_NUMPAD7))
			{
				key = true;
				if (shift)
				{
					sx--;
					sy--;
				}
				else
				{
					movePC(pc.x - 1, pc.y - 1);
				}
				kbd.rebound(KeyEvent.VK_NUMPAD7);
			}
			if (kbd.poll(KeyEvent.VK_NUMPAD1))
			{
				key = true;
				if (shift)
				{
					sx--;
					sy++;
				}
				else
				{
					movePC(pc.x - 1, pc.y + 1);
				}
				kbd.rebound(KeyEvent.VK_NUMPAD1);
			}
			if (kbd.poll(KeyEvent.VK_O))
			{
				key = true;
				openDoor();
				kbd.rebound(KeyEvent.VK_O);
			}
			if (kbd.poll(KeyEvent.VK_PERIOD))
			{
				key = true;
				if (shift)
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
				kbd.rebound(KeyEvent.VK_PERIOD);
			}
			if (kbd.poll(KeyEvent.VK_COMMA))
			{
				key = true;
				if (shift)
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
				kbd.rebound(KeyEvent.VK_COMMA);
			}
			if (kbd.poll(KeyEvent.VK_F))
			{
				key = true;
				if (ctrl)
				{
					selectFont();
				}
				kbd.rebound(KeyEvent.VK_F);
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
		line0 = rlUtl.fill(line0, ' ', 80, false);
		line1 = rlUtl.fill(line1, ' ', 80, false);
		bf.drawStringOSD(new rlPoint(1, 1), line0);
		bf.drawStringOSD(new rlPoint(1, 2), line1);
	}

	private void openDoor()
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
						if (!d.open)
						{
							d.open = true;
							return;
						}
					}
				}
			}
		}
		addMsg("There are no doors to open.");
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

	private void loadData()
	{
		try
		{
			bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
			ps = (PersistenceService)ServiceManager.lookup("javax.jnlp.PersistenceService");
			codebase = bs.getCodeBase();
			FileContents fc = ps.get(codebase);
			InputStream is = fc.getInputStream();
			int bufl = is.read();
			byte[] buf = new byte[bufl];
			is.read(buf);
			int fnSize = is.read();
			is.close();
			String fnName = new String(buf);
			bf.setFont(new Font(fnName, 1, fnSize));
		}
		catch (Exception ex)
		{
			System.err.println("Loading data failed.");
		}
	}

	private void saveData()
	{
		try
		{
			ps.delete(codebase);
		}
		catch (Exception ex)
		{
		}
		try
		{
			byte[] buf = bf.getFont().getFamily().getBytes();
			ps.create(codebase, buf.length + 2);
			FileContents fc = ps.get(codebase);
			OutputStream os = fc.getOutputStream(true);
			os.write(buf.length);
			os.write(buf);
			os.write(bf.getFont().getSize());
			os.close();
		}
		catch (Exception ex)
		{
			System.err.println("Saving data failed.");
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
		String line0 = "", line1 = "";
		line0 = "Player Str: 10";
		if (map.mLvl > 0)
		{
			line1 = "Level: " + map.getID();
		}
		else
		{
			line1 = "Level: " + map.mID;
		}
		line0 = rlUtl.fill(line0, ' ', 80, false);
		line1 = rlUtl.fill(line1, ' ', 80, true);
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
}
