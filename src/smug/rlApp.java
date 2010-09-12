/*
 * smug
 * rlApp.java
 * Copyright (C) vktgz 2010 <vktgz@jabster.pl>
 * License: GPLv3
 */

package smug;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
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

	public rlApp()
	{
		mbuf = new LinkedList<String>();
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
		win.setLocationRelativeTo(null);
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
		bf.scroll(pc.x - 40 + sx, pc.y - 12 + sy);
		drawMsg();
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
			if (kbd.poll(KeyEvent.VK_O))
			{
				key = true;
				openDoor();
				kbd.rebound(KeyEvent.VK_Q);
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
		char fill[];
		if (line0.length() < 80)
		{
			fill = new char[80 - line0.length()];
			Arrays.fill(fill, ' ');
			line0 = line0.concat(new String(fill));
		}
		if (line1.length() < 80)
		{
			fill = new char[80 - line1.length()];
			Arrays.fill(fill, ' ');
			line1 = line1.concat(new String(fill));
		}
		bf.cleanOSD();
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
	}
}
