/*
 * smug
 * rlBuffer.java
 * Copyright (C) vktgz 2010 <vktgz@jabster.pl>
 * License: GPLv3
 */

package smug;

import java.util.ArrayList;

public class rlBuffer
{
	private int cols, rows;
	private ArrayList<ArrayList<rlSymbol>> buf;
	private rlSymbol empty;

	public rlBuffer(int nCols, int nRows, rlSymbol blank)
	{
		cols = nCols;
		rows = nRows;
		empty = blank;
		buf = new ArrayList<ArrayList<rlSymbol>>(rows);
		ArrayList<rlSymbol> tmp;
		for (int r = 0; r < rows; r++)
		{
			tmp = new ArrayList<rlSymbol>(cols);
			for (int c = 0; c < cols; c++)
			{
				tmp.add(empty);
			}
			buf.add(r, tmp);
		}
	}

	public void put(int col, int row, rlSymbol val)
	{
		if ((col > 0) && (col <= cols) && (row > 0) && (row <= rows))
		{
			buf.get(row - 1).set(col - 1, val);
		}
	}

	public rlSymbol get(int col, int row)
	{
		rlSymbol tmp = null;
		if ((col > 0) && (col <= cols) && (row > 0) && (row <= rows))
		{
			tmp = buf.get(row - 1).get(col - 1);
		}
		if (tmp == null)
		{
			tmp = empty;
		}
		return tmp;
	}
}
