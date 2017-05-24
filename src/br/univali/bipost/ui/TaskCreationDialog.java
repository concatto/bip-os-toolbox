package br.univali.bipost.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import br.univali.bipost.Task;

public class TaskCreationDialog extends JDialog {
	private JTextField nameField;
	private JComboBox<Integer> priorityBox;
	private boolean accepted = false;
	
	public TaskCreationDialog(Window owner, String name) {
		super(owner, "New task", ModalityType.APPLICATION_MODAL);
		
		final int min = Task.MIN_PRIORITY;
		final int max = Task.MAX_PRIORITY;
		Integer[] priorities = IntStream.rangeClosed(min, max).boxed().toArray(Integer[]::new);
		
		JPanel root = new JPanel(new BorderLayout());
		JPanel grid = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		nameField = new JTextField(name);
		priorityBox = new JComboBox<>(priorities);
		priorityBox.setSelectedIndex(0);
		
		c.gridx = 0;
		c.gridy = 0;
		grid.add(new JLabel("Name"), c);
		c.gridy = 1;
		grid.add(nameField, c);
		
		c.weightx = 0.2;
		c.insets = new Insets(0, 10, 0, 0);
		c.gridx = 1;
		c.gridy = 0;
		grid.add(new JLabel("Priority"), c);
		c.gridy = 1;
		grid.add(priorityBox, c);
		
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		JButton accept = new JButton("Create");
		JButton cancel = new JButton("Cancel");
		
		cancel.addActionListener(e -> dispose());
		accept.addActionListener(e -> {
			accepted = true;
			dispose();
		});
		
		buttons.add(accept);
		buttons.add(cancel);
		
		root.add(grid, BorderLayout.CENTER);
		root.add(buttons, BorderLayout.SOUTH);
		
		grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
		
		setContentPane(root);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				nameField.requestFocusInWindow();
			}
		});
		
		root.setPreferredSize(new Dimension(240, root.getPreferredSize().height));
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	public Optional<TaskParameters> showAndWait() {
		setVisible(true);

		if (accepted) {
			int priority = priorityBox.getItemAt(priorityBox.getSelectedIndex()).intValue();
			return Optional.of(new TaskParameters(nameField.getText(), priority));
		}
		
		return Optional.empty();
	}
}
