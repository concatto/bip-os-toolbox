package br.univali.bipost.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.io.File;
import java.util.function.BiConsumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LoaderDialog extends JDialog {
	private JPanel root;
	private JPanel sourcePanel;
	private JPanel simulationPanel;
	private JLabel sourceLabel;
	private JLabel simulationLabel;
	private JButton sourceButton;
	private JButton simulationButton;
	private File sourceFile;
	private File simulationFile;
	private JPanel buttonPanel;
	private JButton acceptButton;
	private JButton cancelButton;
	private BiConsumer<File, File> acceptConsumer;

	public LoaderDialog(Window window) {
		super(window, "Select input files", ModalityType.APPLICATION_MODAL);
		
		root = new JPanel();
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		
		sourcePanel = new JPanel(new BorderLayout());
		simulationPanel = new JPanel(new BorderLayout());
		
		sourceLabel = new JLabel("Source: ");
		simulationLabel = new JLabel("Simulation: ");
		
		sourceButton = new JButton("Load...");
		simulationButton = new JButton("Load...");
		
		sourcePanel.add(sourceLabel, BorderLayout.CENTER);
		sourcePanel.add(sourceButton, BorderLayout.EAST);
		sourcePanel.setBorder(BorderFactory.createTitledBorder("Source code"));
		
		simulationPanel.setBorder(BorderFactory.createTitledBorder("Simulation result"));
		simulationPanel.add(simulationLabel, BorderLayout.CENTER);
		simulationPanel.add(simulationButton, BorderLayout.EAST);
		
		JFileChooser chooser = new JFileChooser();
		
		sourceButton.addActionListener(e -> {
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				sourceFile = chooser.getSelectedFile();
				sourceLabel.setText("Source: " + sourceFile.getAbsolutePath());
			}
		});
		
		simulationButton.addActionListener(e -> {
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				simulationFile = chooser.getSelectedFile();
				simulationLabel.setText("Simulation: " + simulationFile.getAbsolutePath());
			}
		});
		
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		acceptButton = new JButton("Accept");
		cancelButton = new JButton("Cancel");
		
		acceptButton.addActionListener(e -> {
			acceptConsumer.accept(sourceFile, simulationFile);
			
			dispose();
			setVisible(false);
		});
		
		cancelButton.addActionListener(e -> dispose());
		
		buttonPanel.add(acceptButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		buttonPanel.add(cancelButton);
		
		root.add(sourcePanel);
		root.add(simulationPanel);
		root.add(buttonPanel);
		
		root.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		setContentPane(root);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		
		setSize(320, getHeight() + 10);
		setLocationRelativeTo(null);
	}
	
	public void setOnAccept(BiConsumer<File, File> consumer) {
		this.acceptConsumer = consumer;
	}
}
