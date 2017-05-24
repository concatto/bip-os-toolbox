package br.univali.bipost.ui;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import br.univali.bipost.Task;

public class TaskCellFactory implements ListCellRenderer<Task> {
	private DefaultListCellRenderer renderer = new DefaultListCellRenderer();

	@Override
	public Component getListCellRendererComponent(JList<? extends Task> list, Task value, int index, boolean isSelected,
			boolean cellHasFocus) {
		
		JLabel l = (JLabel) renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		l.setText(value.getName());
		l.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		return l;
	}
}
