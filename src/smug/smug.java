/*
 * smug
 * smug.java
 * Copyright (C) 2010-2011 vktgz <vktgz@jabster.pl>
 * License: GPLv3
 */

package smug;

import javax.swing.SwingUtilities;

public class smug
{
	public static void main(String args[])
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				rlApp app = new rlApp();
				app.run();
			}
		});
	}
}
