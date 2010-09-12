/*
 * smug
 * rlBattlefield.java
 * Copyright (C) vktgz 2010 <vktgz@jabster.pl>
 * License: GPLv3
 */

package smug;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.JComponent;

public class rlBattlefield
		extends JComponent
		implements Serializable
{
	private int cols, rows;
	private int cWidth, cHeight, cX, cY, sX, sY;
	private rlBuffer buf, osd;
	public boolean rlBuffered;
	private rlSymbol blank;

	public rlBattlefield()
	{
		cols = 10;
		rows = 10;
		sX = 0;
		sY = 0;
		blank = new rlSymbol(' ', rlColor.GRAY, rlColor.BLACK);
		buf = new rlBuffer(cols, rows, blank);
		osd = new rlBuffer(cols, rows, blank);
		rlBuffered = true;
		setBackground(rlColor.BLACK);
		setForeground(rlColor.GRAY);
		setFont(new Font("Monospaced", 0, 10));
	}

	public void setRows(int nRows)
	{
		rows = nRows;
		if (rows < 10)
		{
			rows = 10;
		}
		if (rows > 100)
		{
			rows = 100;
		}
		buf = new rlBuffer(cols, rows, blank);
		osd = new rlBuffer(cols, rows, blank);
		Resize();
	}

	public void setCols(int nCols)
	{
		cols = nCols;
		if (cols < 10)
		{
			cols = 10;
		}
		if (cols > 320)
		{
			cols = 320;
		}
		buf = new rlBuffer(cols, rows, blank);
		osd = new rlBuffer(cols, rows, blank);
		Resize();
	}

	private void Resize()
	{
		FontMetrics fm = getFontMetrics(getFont());
		cWidth = fm.getMaxAdvance();
		cHeight = fm.getHeight();
		cX = 0;
		cY = fm.getMaxAscent();
		Dimension size = new Dimension(cols * cWidth, rows * cHeight);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		Refresh();
	}

	@Override
	public final void setFont(Font font)
	{
		super.setFont(font);
		Resize();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(getBackground());
		g.fillRect(0, 0, cols * cWidth, rows * cHeight);
		g.setColor(getForeground());
		if (buf == null)
		{
			return;
		}
		int x, y;
		for (int c = 1; c <= cols; c++)
		{
			for (int r = 1; r <= rows; r++)
			{
				rlSymbol s = buf.get(c + sX, r + sY);
				x = (c - 1) * cWidth;
				y = (r - 1) * cHeight;
				if (!s.bgColor.equals(getBackground()))
				{
					g.setColor(s.bgColor);
					g.fillRect(x, y, cWidth, cHeight);
				}
				g.setColor(s.fgColor);
				g.drawString(s.str(), x + cX, y + cY);
				s = osd.get(c, r);
				if (!s.equals(blank))
				{
					x = (c - 1) * cWidth;
					y = (r - 1) * cHeight;
					if (!s.bgColor.equals(getBackground()))
					{
						g.setColor(s.bgColor);
						g.fillRect(x, y, cWidth, cHeight);
					}
					g.setColor(s.fgColor);
					g.drawString(s.str(), x + cX, y + cY);
				}
			}
		}
	}

	public void Refresh()
	{
		repaint(0, 0, 0, cols * cWidth, rows * cHeight);
	}

	public void setBuffer(rlBuffer nBuf)
	{
		buf = nBuf;
		if (!rlBuffered)
		{
			Refresh();
		}
	}

	public rlBuffer getBuffer()
	{
		return buf;
	}

	public void drawLine(rlLine l, rlSymbol s)
	{
		rlPoint p;
		for (int i = 0; i < l.length(); i++)
		{
			p = l.get(i);
			buf.put(p.col, p.row, s);
		}
		if (!rlBuffered)
		{
			Refresh();
		}
	}

	public void drawString(rlPoint p, String s)
	{
		for (int i = 0; i < s.length(); i++)
		{
			buf.put(p.col + i, p.row, new rlSymbol(s.charAt(i), getForeground(), getBackground()));
		}
		if (!rlBuffered)
		{
			Refresh();
		}
	}

	public void drawStringOSD(rlPoint p, String s)
	{
		for (int i = 0; i < s.length(); i++)
		{
			osd.put(p.col + i, p.row, new rlSymbol(s.charAt(i), getForeground(), getBackground()));
		}
		if (!rlBuffered)
		{
			Refresh();
		}
	}

	public void cleanOSD()
	{
		osd = new rlBuffer(cols, rows, blank);
	}

	public void drawRect(rlPoint tl, rlPoint br, rlSymbol b)
	{
		drawRect(tl, br, b, null);
	}

	public void drawRect(rlPoint tl, rlPoint br, rlSymbol b, rlSymbol f)
	{
		if (tl.col > br.col)
		{
			return;
		}
		if (tl.row > br.row)
		{
			return;
		}
		for (int c = tl.col; c <= br.col; c++)
		{
			for (int r = tl.row; r <= br.row; r++)
			{
				if ((c == tl.col) || (c == br.col) || (r == tl.row) || (r == br.row))
				{
					buf.put(c, r, b);
				}
				else if (f != null)
				{
					buf.put(c, r, f);
				}
			}
		}
	}

	public void scroll(int x, int y)
	{
		sX = x;
		sY = y;
		Refresh();
	}
}
