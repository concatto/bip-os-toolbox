package br.univali.bipost.ui;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import br.univali.bipost.Simulation;
import br.univali.bipost.SourceLine;
import br.univali.bipost.SystemSource;

public class SimulationTableModel extends DefaultTableModel {
	private static final Vector<String> columns = new Vector<>(
			Arrays.asList("Index", "Address", "Instruction", "Label", "Group")
	);
	
	public SimulationTableModel(Simulation simulation, SystemSource source) {
		Vector<Vector<String>> data = new Vector<>();
		
		for (int i = 0; i < simulation.getIndices().size(); i++) {
			int address =  simulation.getIndices().get(i);
			
			data.add(createRow(source.getLine(address), i, address));
		}
		
		super.setDataVector(data, columns);
	}
	
	public SimulationTableModel(Vector<Vector<String>> data) {
		super(data, columns);
	}
	
	public SimulationTableModel() {
		this(new Vector<Vector<String>>());
	}

	public static Vector<String> createRow(SourceLine line, int index, int address) {
		Vector<String> row = new Vector<>();
		
		row.add(String.format("0x%04X", index));
		row.add(String.format("0x%04X", address));
		row.add(line.getInstruction());
		row.add(line.getLabel().getName());
		row.add(line.getLabel().getGroup().getName());
		
		return row;
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}
