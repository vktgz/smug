/*
 * smug
 * rlItem.java
 * Copyright (C) 2010-2011 vktgz <vktgz@jabster.pl>
 * License: GPLv3
 */

package smug;

public class rlItem
		extends rlObj
{
	public enum Kind
	{
		GOLD, ARMOR, WEAPON
	}
	//
	public Kind kind;
	public int pcs;
	public String name;

	public rlItem(rlSymbol nsmb, int nx, int ny)
	{
		super(Type.ITEM, nsmb, nx, ny);
		pcs = 1;
	}

	public String getName()
	{
		return Integer.toString(pcs).concat(" pcs of ").concat(name);
	}

	public static rlItem makeGold(int cnt, int nx, int ny)
	{
		rlItem gold = new rlItem(new rlSymbol('$', rlColor.YELLOW, rlColor.BLACK), nx, ny);
		gold.kind = Kind.GOLD;
		gold.pcs = cnt;
		gold.name = "gold coin";
		return gold;
	}
}
