package br.univali.bipost.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import br.univali.bipost.AssemblyLabel;
import br.univali.bipost.Simulation;
import br.univali.bipost.SourceLine;
import br.univali.bipost.SystemSource;

public class TimelinePanel extends JPanel {
	private JPanel pointerPanel;
	private Consumer<Integer> seekConsumer;
	private Graphics2D graphics;
	private BufferedImage image;
	private int pointerIndex;
	public static final int SLICE_WIDTH = 1;
	public static final int SLICE_HEIGHT = 26;
	private Simulation simulation;
	private SystemSource source;

	public TimelinePanel() {
		super(null);		
		
		pointerPanel = new JPanel();
		pointerPanel.setForeground(Color.GRAY);
		pointerPanel.setBackground(Color.GRAY);
		
		add(pointerPanel);
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				seekPointer(pointerIndex);
			}
		});
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int i = e.getX() / SLICE_WIDTH; //Round down
				seekPointer(i);
				seekConsumer.accept(i);
			}
		});
	}
	
	public void load(Simulation simulation, SystemSource source) {
		this.simulation = simulation;
		this.source = source;
		
		int width = simulation.getIndices().size() * SLICE_WIDTH; 
		int height = (source.getGroups().size() + 1) * SLICE_HEIGHT;
		
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		graphics = image.createGraphics();
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, width, height);
		
		for (int i = 1; i <= source.getGroups().size(); i++) {
			int y = i * SLICE_HEIGHT;
			graphics.setColor(Color.DARK_GRAY);
			graphics.drawLine(0, y, image.getWidth(), y);
		}
		
		Point oldPoint = null;
		for (int i = 0; i < simulation.getIndices().size(); i++) {
			Point p = computePoint(i);
			
			if (i % 0xFF == 0 && i > 0) {
				graphics.setColor(Color.DARK_GRAY);
				graphics.drawLine(p.x, 0, p.x, image.getHeight() - SLICE_HEIGHT);
				
				String s = String.format("0x%04X", i);
				int sWidth = graphics.getFontMetrics().stringWidth(s);
				graphics.setColor(Color.LIGHT_GRAY.brighter());
				graphics.drawString(s, p.x - (sWidth / 2), image.getHeight() - 10);
			}
			
			graphics.setColor(Color.GREEN);
			graphics.drawLine(p.x, p.y, p.x + SLICE_WIDTH, p.y);
			
			if (oldPoint != null && oldPoint.y != p.y) {
				graphics.drawLine(p.x, p.y, p.x, oldPoint.y);
			}
			
			oldPoint = p;
		}
		
		setBackground(Color.BLACK);
		setForeground(Color.BLACK);
		seekPointer(0);
		
		graphics.dispose();
	}

	public void seekPointer(int i) {
		pointerIndex = i;
		int x = i * SLICE_WIDTH;
		
		pointerPanel.setBounds(x, 0, 1, getHeight());
	}
	
	private Point computePoint(int i) {
		SourceLine line = source.getLine(simulation.getIndices().get(i));
		int x = i * SLICE_WIDTH;
		int y = findLabel(line.getLabel()) * SLICE_HEIGHT;
		y += (SLICE_HEIGHT / 2);
		
		return new Point(x, y);
	}
	
	private int findLabel(AssemblyLabel label) {
		for (int i = 0; i < source.getGroups().size(); i++) {
			if (label.getGroup().equals(source.getGroups().get(i))) {
				return i;
			}
		}
		
		return -1;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (image == null) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getWidth(), getHeight());
		} else {
			g.drawImage(image, 0, 0, null);
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		if (image != null) {
			return new Dimension(image.getWidth(), image.getHeight());
		}
		
		return super.getPreferredSize();
	}
	
	public void onSeek(Consumer<Integer> consumer) {
		this.seekConsumer = consumer;
	}
}
