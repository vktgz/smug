/*
 * smug
 * rlPoint.java
 * Copyright (C) vktgz 2010 <vktgz@jabster.pl>
 * License: GPLv3
 */

package smug;

public class rlPoint
{
	public int col, row;

	public rlPoint(int nCol, int nRow)
	{
		col = nCol;
		row = nRow;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof rlPoint)
		{
			rlPoint tmp = (rlPoint)obj;
			if ((col == tmp.col) && (row == tmp.row))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 61 * hash + this.col;
		hash = 61 * hash + this.row;
		return hash;
	}
}
