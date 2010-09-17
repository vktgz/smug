/*
 * smug
 * rlDoor.java
 * Copyright (C) vktgz 2010 <vktgz@jabster.pl>
 * License: GPLv3
 */

package smug;

public class rlDoor
		extends rlObj
{
	public enum Dir
	{
		N, S, W, E, U, D
	}
	//
	public enum Kind
	{
		ROOM, TUNN, PASS
	}
	//
	public Dir dir;
	public Kind kind;
	public boolean open;
	public String dID;
	public int dLvl;

	public rlDoor(Dir ndir, Kind nkind, int nx, int ny)
	{
		super(Type.DOOR, new rlSymbol('+', rlColor.BROWN, rlColor.BLACK), nx, ny);
		open = false;
		dir = ndir;
		kind = nkind;
		if (dir == Dir.U)
		{
			smb.code = '<';
			smb.fgColor = rlColor.GRAY;
			open = true;
		}
		if (dir == Dir.D)
		{
			smb.code = '>';
			smb.fgColor = rlColor.GRAY;
			open = true;
		}
		dID = "";
		dLvl = 0;
	}

	@Override
	public rlSymbol getSymbol()
	{
		if (kind != Kind.PASS)
		{
			if (open)
			{
				smb.code = '/';
			}
			else
			{
				smb.code = '+';
			}
		}
		return super.getSymbol();
	}

	public void setID(String nID, int nLvl)
	{
		dID = nID;
		dLvl = nLvl;
	}

	public String getID()
	{
		return dID + Integer.toString(dLvl);
	}
}
