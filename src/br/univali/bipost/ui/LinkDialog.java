package br.univali.bipost.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.io.File;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

public class LinkDialog extends JDialog {
	private JPanel root;
	private JPanel logPanel;
	private JLabel logLabel;
	private JTextArea logArea;
	private JPanel pathPanel;
	private JLabel pathLabel;
	private JButton chooseButton;
	private JButton acceptButton;
	private JButton closeButton;
	private JPanel buttonsPanel;
	private Runnable startAction;
	private Consumer<File> exportAction;
	private JPanel topPanel;
	private String path;
	private boolean linked = false;

	public LinkDialog(Window owner) {
		super(owner, "Linking phase", ModalityType.APPLICATION_MODAL);
		
		root = new JPanel(new BorderLayout(0, 10));
		
		root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		root.setPreferredSize(new Dimension(700, 500));
		
		logPanel = new JPanel(new BorderLayout(0, 10));
		logLabel = new JLabel("Log");
		logArea = new JTextArea();
		
		logPanel.add(logLabel, BorderLayout.NORTH);
		logPanel.add(logArea, BorderLayout.CENTER);
		
		pathPanel = new JPanel(new BorderLayout());
		pathLabel = new JLabel();
		chooseButton = new JButton("Choose");
		
		pathPanel.add(pathLabel, BorderLayout.CENTER);
		pathPanel.add(chooseButton, BorderLayout.EAST);
		
		acceptButton = new JButton();
		closeButton = new JButton("Close");
		buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		buttonsPanel.add(acceptButton);
		buttonsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonsPanel.add(closeButton);
		
		topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		topPanel.add(pathPanel);
		topPanel.add(Box.createRigidArea(new Dimension(0, 15)));
		topPanel.add(new JSeparator());
		
		root.add(topPanel, BorderLayout.NORTH);
		root.add(logPanel, BorderLayout.CENTER);
		root.add(buttonsPanel, BorderLayout.SOUTH);
		
		chooseButton.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			int option = chooser.showOpenDialog(SwingUtilities.getWindowAncestor(this));
			if (option == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();

				setPath(file.getAbsolutePath());
				publishMessage("File chosen: " + path + ".");
				publishMessage("Click the \"Start\" button to start linking the tasks with the OS.");
			}
		});
		
		logArea.setEditable(false);
		logArea.setLineWrap(true);
		logArea.setWrapStyleWord(true);
		
		closeButton.addActionListener(e -> dispose());

		acceptButton.addActionListener(e -> {
			if (linked) {
				JFileChooser chooser = new JFileChooser();
				int option = chooser.showSaveDialog(SwingUtilities.getWindowAncestor(this));
				
				if (option != JFileChooser.APPROVE_OPTION) {
					exportAction.accept(chooser.getSelectedFile());
				}
			} else {
				acceptButton.setEnabled(false);
				startAction.run();
			}
		});
		
		((DefaultCaret) logArea.getCaret()).setUpdatePolicy(DefaultCaret.OUT_BOTTOM);
		
		reset();
		
		setContentPane(root);
		setSize(new Dimension(550, 600));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
	private void setPath(String path) {
		this.path = path;
		pathLabel.setText("OS path: " + path);
		if (!path.isEmpty()) {
			acceptButton.setEnabled(true);
		}
	}
	
	public String getPath() {
		return path;
	}

	public void reset() {
		logArea.setText("");
		setPath("");
		
		notifyResult(false);
		
		publishMessage("Ready. Choose the file containing the source code of the Operating System.");
	}
	
	public void publishMessage(String message) {
		if (!logArea.getText().isEmpty()) {
			logArea.append("\n");
		}

		logArea.append(message);
	}
	
	public void notifyResult(boolean success) {
		acceptButton.setText(success ? "Export" : "Start");
		acceptButton.setEnabled(success);
		
		if (success) {
			publishMessage("Use the \"Export\" button to save the resulting source code in a file.");
		}
	}
	
	public void setOnStart(Runnable action) {
		this.startAction = action;
	}
	
	public void setOnExport(Consumer<File> action) {
		this.exportAction = action;
	}
}
