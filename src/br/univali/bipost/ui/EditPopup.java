package br.univali.bipost.ui;

import java.awt.Window;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import br.univali.bipost.Task;

public class EditPopup extends JPopupMenu {
	private ButtonGroup priorityGroup;
	private List<JMenuItem> items;
	private Runnable deleteAction;
	private Consumer<String> renameConsumer;
	private Consumer<Integer> priorityConsumer;
	private Task task;

	public EditPopup(Window owner) {
		JMenu priorityMenu = new JMenu("Priority");
		priorityGroup = new ButtonGroup();
		
		items = IntStream.rangeClosed(Task.MIN_PRIORITY, Task.MAX_PRIORITY)
					.mapToObj(String::valueOf)
					.map(s -> new JRadioButtonMenuItem(s))
					.collect(Collectors.toList());
		
		items.forEach(item -> {
			priorityGroup.add(item);
			priorityMenu.add(item);
			item.addActionListener(e -> priorityConsumer.accept(Integer.parseInt(item.getText())));
		});
		
		JMenuItem renameItem = new JMenuItem("Rename...");
		JMenuItem deleteItem = new JMenuItem("Delete");
		
		renameItem.addActionListener(e -> {
			String name = JOptionPane.showInputDialog(owner, "New name", "Rename");
			if (name != null) {
				renameConsumer.accept(name);
			}
		});
		
		deleteItem.addActionListener(e -> {
			String message = "Are you sure you want to delete task " + task.getName() + "?";
			int option = JOptionPane.showConfirmDialog(owner, message, "Confirmation", JOptionPane.YES_NO_OPTION);
			
			if (option == JOptionPane.YES_OPTION) {
				deleteAction.run();
			}
		});
		
		add(renameItem);
		add(priorityMenu);
		addSeparator();
		add(deleteItem);
	}

	public void setCurrentTask(Task task) {
		this.task = task;
		
		JMenuItem item = items.get(task.getPriority() - Task.MIN_PRIORITY);
		priorityGroup.setSelected(item.getModel(), true);
	}
	
	public void onRename(Consumer<String> renameConsumer) {
		this.renameConsumer = renameConsumer;
	}
	
	public void onDelete(Runnable deleteAction) {
		this.deleteAction = deleteAction;	
	}
	
	public void onPriorityChange(Consumer<Integer> priorityConsumer) {
		this.priorityConsumer = priorityConsumer;
		
	}
}
