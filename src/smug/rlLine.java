/*
 * smug
 * rlLine.java
 * Copyright (C) 2010-2011 vktgz <vktgz@jabster.pl>
 * License: GPLv3
 */

package smug;

import java.util.ArrayList;

public class rlLine
{
	private ArrayList<rlPoint> buf;

	public rlLine(rlPoint bp, rlPoint ep)
	{
		buf = new ArrayList<rlPoint>();
		add(bp);
		double w = Math.sqrt(Math.pow((ep.col - bp.col), 2) + Math.pow((ep.row - bp.row), 2));
		float d = (float)(1 / (10 * w));
		int c, r;
		for (float t = 0; t <= 1; t = t + d)
		{
			c = Math.round(((1 - t) * bp.col) + (t * ep.col));
			r = Math.round(((1 - t) * bp.row) + (t * ep.row));
			add(new rlPoint(c, r));
		}
		add(ep);
	}

	private void add(rlPoint val)
	{
		if (!buf.contains(val))
		{
			buf.add(val);
		}
	}

	public int length()
	{
		return buf.size();
	}

	public rlPoint get(int idx)
	{
		rlPoint tmp = null;
		if ((idx >= 0) && (idx < buf.size()))
		{
			tmp = buf.get(idx);
		}
		return tmp;
	}

	@Override
	public boolean equals(Object obj)
	{
		boolean res = (obj instanceof rlLine);
		if (res)
		{
			rlLine tmp = (rlLine)obj;
			res = (length() == tmp.length());
			if (res)
			{
				for (int i = 0; i < length(); i++)
				{
					res = res && (get(i).equals(tmp.get(i)));
				}
			}
		}
		return res;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 47 * hash + (this.buf != null ? this.buf.hashCode() : 0);
		return hash;
	}
}
