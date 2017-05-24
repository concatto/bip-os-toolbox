package br.univali.bipost.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

public class CodeGroupPanel extends JPanel {
	private static final int GROUP_WIDTH = 150;
	private List<String> groups;

	public CodeGroupPanel() {
		setPreferredSize(new Dimension(GROUP_WIDTH, getPreferredSize().height));
	}
	
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		if (groups != null) {
			for (int i = 0; i < groups.size(); i++) {
				String s = groups.get(i);
				int height = g.getFontMetrics().getAscent();
				int yString = i * TimelinePanel.SLICE_HEIGHT + (TimelinePanel.SLICE_HEIGHT / 2) + (height / 2);
				
				g.setColor(Color.WHITE);
				g.drawString(s, 5, yString);
				
				int y = (i + 1) * TimelinePanel.SLICE_HEIGHT + 1;
				g.setColor(Color.DARK_GRAY);
				g.drawLine(0, y, getWidth(), y);
			}
		}
	}
}