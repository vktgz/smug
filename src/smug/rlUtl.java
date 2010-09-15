/*
 * smug
 * rlUtl.java
 * Copyright (C) vktgz 2010 <vktgz@jabster.pl>
 * License: GPLv3
 */

package smug;

import java.util.Arrays;

public class rlUtl
{
	public static String fill(String str, char c, int len, boolean beg)
	{
		String fstr = "";
		if (str.length() < len)
		{
			char flc[] = new char[len - str.length()];
			Arrays.fill(flc, c);
			fstr = new String(flc);
		}
		if (beg)
		{
			return fstr.concat(str);
		}
		else
		{
			return str.concat(fstr);
		}
	}
}
