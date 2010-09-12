/*
 * smug
 * rlObj.java
 * Copyright (C) vktgz 2010 <vktgz@jabster.pl>
 * License: GPLv3
 */

package smug;

import java.util.ArrayList;

public class rlObj
{
	public enum Type
	{
		WALL, FLOOR, DOOR, CHAR
	}
	//
	public Type id;
	protected rlSymbol smb;
	public ArrayList<rlObj> items;
	public ArrayList<rlChar> chars;
	public int x, y, time;
	private boolean visible;

	public rlObj(Type nid, rlSymbol nsmb, int nx, int ny)
	{
		id = nid;
		smb = nsmb;
		items = new ArrayList<rlObj>();
		chars = new ArrayList<rlChar>();
		x = nx;
		y = ny;
		time = 0;
		visible = false;
	}

	public rlSymbol getSymbol()
	{
		rlSymbol res = rlSymbol.FOG;
		if (visible)
		{
			res = smb;
			if (!items.isEmpty())
			{
				res = items.get(items.size() - 1).getSymbol();
			}
			if (!chars.isEmpty())
			{
				res = chars.get(chars.size() - 1).getSymbol();
			}
		}
		return res;
	}

	@Override
	public boolean equals(Object o)
	{
		boolean eq = false;
		rlObj tmp;
		if (o instanceof rlObj)
		{
			tmp = (rlObj)o;
			eq = ((tmp.x == x) && (tmp.y == y) && (tmp.id == id));
		}
		return eq;
	}

	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
		hash = 97 * hash + this.x;
		hash = 97 * hash + this.y;
		return hash;
	}

	public void resetTime()
	{
		time = 0;
	}

	public boolean getVisible()
	{
		return visible;
	}

	public void setVisible(boolean content, boolean val)
	{
		if (content)
		{
			for (int i = 0; i < items.size(); i++)
			{
				items.get(i).setVisible(false, val);
			}
			for (int i = 0; i < chars.size(); i++)
			{
				chars.get(i).setVisible(false, val);
			}
		}
		else
		{
			visible = val;
		}
	}
}
