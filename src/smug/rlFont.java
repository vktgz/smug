/*
 * smug
 * rlFont.java
 * Copyright (C) vktgz 2010 <vktgz@jabster.pl>
 * License: GPLv3
 */

package smug;

import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class rlFont
		extends JDialog
		implements ItemListener, ActionListener
{
	private rlBattlefield bf;
	private JComboBox nameCmb, sizeCmb;
	private JButton okBtn, quitBtn;
	public boolean ok;

	public rlFont(Frame owner)
	{
		super(owner);
		ok = false;
		setTitle("Select font");
		setModal(true);
		setResizable(false);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		JPanel fontPane = new JPanel();
		fontPane.setLayout(new BoxLayout(fontPane, BoxLayout.X_AXIS));
		fontPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Font name and size"));
		nameCmb = new JComboBox();
		sizeCmb = new JComboBox();
		String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (int i = 0; i < fontNames.length; i++)
		{
			nameCmb.addItem(fontNames[i]);
		}
		for (int i = 6; i < 51; i = i + 2)
		{
			sizeCmb.addItem(i);
		}
		fontPane.add(nameCmb);
		fontPane.add(sizeCmb);
		getContentPane().add(fontPane);
		JPanel bfPane = new JPanel();
		bfPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Preview"));
		bf = new rlBattlefield();
		bf.setCols(10);
		bf.setRows(10);
		rlBuffer buf = new rlBuffer(10, 10, new rlSymbol(' ', rlColor.GRAY, rlColor.BLACK));
		bf.setBuffer(buf);
		bf.drawString(new rlPoint(1, 1), "Doors are ");
		bf.drawString(new rlPoint(1, 2), "  ########");
		bf.drawString(new rlPoint(1, 3), "###......#");
		bf.drawString(new rlPoint(1, 4), ".........#");
		bf.drawString(new rlPoint(1, 5), "###...<..#");
		bf.drawString(new rlPoint(1, 6), "  #......#");
		bf.drawString(new rlPoint(1, 7), "  #......#");
		bf.drawString(new rlPoint(1, 8), "  #......#");
		bf.drawString(new rlPoint(1, 9), "  ########");
		bf.drawString(new rlPoint(1, 10), "PC Str: 10");
		buf.put(3, 4, new rlSymbol('+', rlColor.BROWN, rlColor.BLACK));
		buf.put(4, 5, new rlSymbol('@', rlColor.DGRAY, rlColor.BLACK));
		buf.put(6, 7, new rlSymbol('k', rlColor.LGREEN, rlColor.BLACK));
		bfPane.add(bf);
		getContentPane().add(bfPane);
		JPanel btnPane = new JPanel();
		btnPane.setLayout(new BoxLayout(btnPane, BoxLayout.X_AXIS));
		okBtn = new JButton("OK");
		quitBtn = new JButton("Cancel");
		btnPane.add(okBtn);
		btnPane.add(quitBtn);
		getContentPane().add(btnPane);
	}

	@Override
	public void setFont(Font f)
	{
		nameCmb.setSelectedItem(f.getFamily());
		sizeCmb.setSelectedItem(f.getSize());
		nameCmb.addItemListener(this);
		sizeCmb.addItemListener(this);
		bf.setFont(f);
		pack();
		okBtn.addActionListener(this);
		quitBtn.addActionListener(this);
	}

	@Override
	public Font getFont()
	{
		return bf.getFont();
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		bf.setFont(new Font((String)nameCmb.getSelectedItem(), 1, (Integer)sizeCmb.getSelectedItem()));
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == okBtn)
		{
			ok = true;
		}
		setVisible(false);
	}
}
