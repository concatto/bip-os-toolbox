package br.univali.bipost.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import br.univali.bipost.Simulation;
import br.univali.bipost.SystemSource;

public class SimulationViewer extends JFrame {
	private enum State { WAITING, RUNNING, PAUSED };
	
	private JPanel root;
	private JPanel topPanel;
	private JPanel centerPanel;
	private JPanel bottomPanel;
	private JButton stopButton;
	private JButton playPauseButton;
	private JLabel streamLabel;
	private JTable streamTable;
	private TimelinePanel timelinePanel;
	private CodeGroupPanel groupPanel;
	private JProgressBar bar;
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private State state;
	private ScheduledFuture<?> task;
	
	public SimulationViewer() {
		super("Simulation Viewer");
		
		streamLabel = new JLabel("Instruction stream");
		streamTable = new JTable(new SimulationTableModel());
		
		streamTable.setRowHeight(20);
		streamTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		streamTable.setRowSelectionAllowed(true);
		streamTable.getSelectionModel().addListSelectionListener(e -> {
			int index = streamTable.getSelectedRow();
			timelinePanel.seekPointer(index);
			bar.setValue(index);
		});
		
		root = new JPanel(new BorderLayout(0, 10));
		topPanel = new JPanel(new BorderLayout(10, 0));
		centerPanel = new JPanel(new BorderLayout());
		bottomPanel = new JPanel(new BorderLayout(0, 5));
		
		timelinePanel = new TimelinePanel();
		timelinePanel.onSeek(this::selectRow);
		
		JScrollPane timelineScroll = new JScrollPane(timelinePanel,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		groupPanel = new CodeGroupPanel();
		
		centerPanel.add(groupPanel, BorderLayout.WEST);
		centerPanel.add(timelineScroll, BorderLayout.CENTER);
		
		JScrollPane tableScroll = new JScrollPane(streamTable);
		tableScroll.setPreferredSize(new Dimension(tableScroll.getPreferredSize().width, 200));
		
		bottomPanel.add(streamLabel, BorderLayout.NORTH);
		bottomPanel.add(tableScroll, BorderLayout.CENTER);
		
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		stopButton = new JButton("Stop");
		playPauseButton = new JButton("Play/Pause");
		
		buttonsPanel.add(stopButton);
		buttonsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		buttonsPanel.add(playPauseButton);
		
		bar = new JProgressBar();
		topPanel.add(bar, BorderLayout.CENTER);
		topPanel.add(buttonsPanel, BorderLayout.WEST);
		
		root.add(topPanel, BorderLayout.NORTH);
		root.add(centerPanel, BorderLayout.CENTER);
		root.add(bottomPanel, BorderLayout.SOUTH);
		root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem loadItem = new JMenuItem("Load files...");
		
		loadItem.addActionListener(e -> {
			LoaderDialog d = new LoaderDialog(this);
			d.setOnAccept((src, sim) -> {
				try {
					loadData(src, sim);
					changeState(State.PAUSED);
				} catch (IOException e1) {
					e1.printStackTrace();
				}	
			});
			d.setVisible(true);
		});
	
		playPauseButton.addActionListener(e -> {
			if (state == State.PAUSED) {
				task = executor.scheduleAtFixedRate(() -> SwingUtilities.invokeLater(() -> {
					selectRow(streamTable.getSelectedRow() + 1);
				}), 0, 100, TimeUnit.MILLISECONDS);
				
				changeState(State.RUNNING);
			} else if (state == State.RUNNING) {
				task.cancel(true);
				
				changeState(State.PAUSED);
			}
		});
		
		stopButton.addActionListener(e -> {
			if (task != null) {
				task.cancel(true);
			}
			
			selectRow(0);
			changeState(State.PAUSED);
		});
		
		fileMenu.add(loadItem);
		menuBar.add(fileMenu);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				executor.shutdownNow();
			}
		});
		
		setJMenuBar(menuBar);
		setContentPane(root);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(new Dimension(800, 600));
		setLocationRelativeTo(null);
		
		changeState(State.WAITING);
	}
	
	private void selectRow(int i) {
		streamTable.setRowSelectionInterval(i, i);
		streamTable.scrollRectToVisible(streamTable.getCellRect(i, 0, true));
	}

	private void changeState(State state) {
		this.state = state;
		stopButton.setEnabled(state != State.WAITING);
		playPauseButton.setEnabled(state != State.WAITING);
	}
	
	private void loadData(File src, File sim) throws IOException {
		Simulation s = new Simulation(sim.getAbsolutePath());
		SystemSource source = new SystemSource(src.getAbsolutePath());
		
		groupPanel.setGroups(
				source.getGroups().stream().map(g -> g.getName()).collect(Collectors.toList())
		);
		
		timelinePanel.load(s, source);
		streamTable.setModel(new SimulationTableModel(s, source));
		streamTable.setRowSelectionInterval(0, 0);
		
		bar.setMaximum(streamTable.getRowCount() - 1);
		
		timelinePanel.revalidate();
		root.revalidate();
		root.repaint();
	}
}
