/*
 * smug
 * rlChar.java
 * Copyright (C) vktgz 2010 <vktgz@jabster.pl>
 * License: GPLv3
 */

package smug;

public class rlChar
		extends rlObj
{
	public enum Kind
	{
		PC, NPC, PET, MON
	}
	//
	public Kind kind;

	public rlChar(Kind nkind, rlSymbol nsymb)
	{
		super(Type.CHAR, nsymb, 0, 0);
		kind = nkind;
		time = 100;
	}

	@Override
	public void resetTime()
	{
		time = 100;
	}

	void action()
	{
		//
	}
}
