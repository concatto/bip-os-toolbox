package br.univali.bipost.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import br.univali.bipost.Program;
import br.univali.bipost.Task;

public class EditorFrame extends JFrame {
	private static final String DEFAULT_TASK_LABEL = "<no task selected>";
	private JPanel root;
	private JMenu fileMenu;
	private JMenu editMenu;
	private JMenuBar menuBar;
	private JLabel currentTaskLabel;
	private JTextArea taskTextArea;
	private JPanel leftPane;
	private JLabel taskLabel;
	private JList<Task> taskList;
	private JPanel rightPane;
	private JSplitPane splitter;
	private JMenuItem linkMenu;
	private LinkDialog linkDialog;
	private DefaultListModel<Task> model;

	public EditorFrame() {
		super("BIP/OS Toolbox");
		setSize(new Dimension(1024, 768));
		
		model = new DefaultListModel<>();
		
		root = new JPanel(new BorderLayout());
		
		linkDialog = new LinkDialog(this);
		
		fileMenu = new JMenu("File");
		editMenu = new JMenu("Edit");
		menuBar = new JMenuBar();
		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		
		linkMenu = new JMenuItem("Link");
		linkMenu.addActionListener(e -> {
			linkDialog.reset();
			linkDialog.setVisible(true);
		});
		
		fileMenu.add(linkMenu);
		
		currentTaskLabel = new JLabel(DEFAULT_TASK_LABEL);
		taskTextArea = new JTextArea();
		leftPane = new JPanel(new BorderLayout(0, 20));
		leftPane.add(taskTextArea, BorderLayout.CENTER);
		leftPane.add(currentTaskLabel, BorderLayout.NORTH);
		
		taskLabel = new JLabel();
		taskList = new JList<>();
		rightPane = new JPanel(new BorderLayout());
		rightPane.add(taskList, BorderLayout.CENTER);
		rightPane.add(taskLabel, BorderLayout.NORTH);
		
		splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
		splitter.setDividerLocation((int) Math.round(getWidth() * 0.75));
		
		root.add(splitter, BorderLayout.CENTER);
		
		leftPane.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));
		taskLabel.setBorder(BorderFactory.createEmptyBorder(22, 20, 22, 20));
		
		Font font = new Font("Verdana", Font.PLAIN, 20);
		currentTaskLabel.setFont(font.deriveFont(28f));
		taskLabel.setFont(font);
		
		rightPane.setMinimumSize(new Dimension(200, 0));
		
		setJMenuBar(menuBar);
		setContentPane(root);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		updateLabels();
		
		addTask(new Task("TSK0", "abc", 2));
		
		taskList.setCellRenderer(new TaskCellFactory());
		taskList.setModel(model);
		
		taskList.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				Task t = getCurrentTask();
				boolean exists = t != null;
				
				taskTextArea.setText(exists ? t.getSourceCode() : "");
				currentTaskLabel.setEnabled(exists);
				taskTextArea.setEnabled(exists);
				
				updateLabels();
			}
		});
		
		JPopupMenu createPopup = new JPopupMenu();
		JMenuItem createItem = new JMenuItem("New task...");
		
		createPopup.add(createItem);
		createItem.addActionListener(e -> {
			TaskCreationDialog dialog = new TaskCreationDialog(this, "TSK" + model.getSize());
			
			dialog.showAndWait().ifPresent(p -> {
				addTask(new Task(p.getName(), p.getPriority()));
			});
		});
		
		EditPopup editPopup = new EditPopup(this);
		editPopup.onRename(s -> updateCurrentTask(t -> t.setName(s)));
		editPopup.onPriorityChange(p -> updateCurrentTask(t -> t.setPriority(p)));
		editPopup.onDelete(() -> model.remove(taskList.getSelectedIndex()));
		
		taskList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					int index = taskList.locationToIndex(e.getPoint());
					JPopupMenu actualMenu;
					if (index >= 0 && taskList.getCellBounds(index, index).contains(e.getPoint())) {
						taskList.setSelectedIndex(index);
						
						actualMenu = editPopup;
						editPopup.setCurrentTask(getCurrentTask());
					} else {
						actualMenu = createPopup;
					}
					
					actualMenu.show(taskList, e.getX(), e.getY());
				}
			}
		});
		
		currentTaskLabel.setEnabled(false);
		taskTextArea.setEnabled(false);
		
		taskTextArea.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) { update(); }
			public void insertUpdate(DocumentEvent e) { update(); }
			public void changedUpdate(DocumentEvent e) { update(); }
			
			private void update() {
				model.get(taskList.getSelectedIndex()).setSourceCode(taskTextArea.getText());
			}
		});
	}
	
	private void updateCurrentTask(Consumer<Task> consumer) {
		consumer.accept(getCurrentTask());
		model.setElementAt(getCurrentTask(), taskList.getSelectedIndex());
		updateLabels();
	}

	private void addTask(Task task) {
		model.addElement(task);
		updateLabels();
		
		taskList.setSelectedIndex(model.getSize() - 1);
	}
	
	private Task getCurrentTask() {
		int index = taskList.getSelectedIndex();
		
		if (index < 0) {
			return null;
		}
		
		return model.get(index);
	}

	private void updateLabels() {
		int index = taskList.getSelectedIndex();
		
		if (index < 0) {
			currentTaskLabel.setText(DEFAULT_TASK_LABEL);
		} else {
			Task t = taskList.getModel().getElementAt(index);
			String text = String.format("Task: %s (P=%d)", t.getName(), t.getPriority());
			currentTaskLabel.setText(text);
		}
		
		taskLabel.setText(String.format("Tasks: %d/%d", model.getSize(), Program.MAX_TASKS));
	}

	public LinkDialog getLinkPanel() {
		return linkDialog;
	}
	
	public List<Task> getTasks() {
		return Collections.list(model.elements());
	}
}
