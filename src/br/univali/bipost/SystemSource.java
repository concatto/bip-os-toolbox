package br.univali.bipost;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemSource {
	private List<InstructionGroup> groups;
	private List<SourceLine> lines;
	private List<AssemblyLabel> labels;
	private Map<String, AssemblyLabel> labelMap;

	public SystemSource(String filePath) throws IOException {
		lines = new ArrayList<>();
		groups = new ArrayList<>();
		labels = new ArrayList<>();
		labelMap = new HashMap<>();
		
		boolean found = false;
		AssemblyLabel currentLabel = addLabel(new AssemblyLabel());
		
		BufferedReader r = new BufferedReader(new FileReader(filePath));
		String line;
		
		while ((line = r.readLine()) != null) {
			line = line.trim();
			
			if (found) {
				if (!line.isEmpty() && !line.startsWith("#")) {
					if (isLabel(line)) {
						String name = line.substring(0, line.indexOf(":")).trim();
						currentLabel = addLabel(new AssemblyLabel(name, lines.size()));
					} else {
						lines.add(new SourceLine(line, currentLabel));
					}
				}
			}
			
			if (line.contains(".text")) {
				found = true;
			}
		}
		
		r.close();
		
		createGroups();
		applyGroups();
	}
	
	private AssemblyLabel addLabel(AssemblyLabel label) {
		labels.add(label);
		labelMap.put(label.getName(), label);
		return label;
	}
	
	private void createGroups() {
		groups.add(new InstructionGroup("Operating System")); //Must be the first
		
		boolean hendrig = true;
		if (hendrig) {
			addTasksManually();
		} else {
			addTasksAutomatically();
		}
		
		groups.add(new InstructionGroup("Context Switch",
				createDelimiter("OS_TSK_PAUSE", "OS_TSK_END"),
				createDelimiter("SET_STATUS", "BUBBLE_SORT"),
				createDelimiter("SCHEDULER", "OS_END")
		));
	}

	private LabelDelimiter createDelimiter(String start, String end) {
		return new LabelDelimiter(labelMap.get(start), labelMap.get(end));
	}

	private void addTasksAutomatically() {
		// TODO Auto-generated method stub
	}

	private void addTasksManually() {
		groups.add(new InstructionGroup("Task 1", createDelimiter("F1_0", "END_TSK_1")));
		groups.add(new InstructionGroup("Task 2", createDelimiter("INIT_TSK_2", "END_TSK_2")));
		groups.add(new InstructionGroup("Task 3", createDelimiter("INIT_TSK_3", "END_TSK_3")));
	}

	private void applyGroups() {
		InstructionGroup currentGroup = null;
		
		for (AssemblyLabel label : labels) {
			//If we belong to a group, check if this is the end boundary
			if (currentGroup != null) {
				for (InstructionGroup group : groups) {
					if (group.testEnding(label)) {
						currentGroup = null;
						break;
					}
				}
			}
			
			//If we do not belong to a group, see if the label is a starting point
			if (currentGroup == null) {
				for (InstructionGroup group : groups) {
					if (group.testStarting(label)) {
						currentGroup = group;
						break;
					}
				}
			}
			
			label.setGroup(currentGroup == null ? groups.get(0) : currentGroup);
		}
	}
	
	public SourceLine getLine(int index) {
		return lines.get(index);
	}

	private static boolean isLabel(String line) {
		int colon = line.indexOf(":");
		int comment = line.indexOf("#");
		return (colon >= 0 && (comment < 0 || colon < comment));
	}

	public List<InstructionGroup> getGroups() {
		return groups;
	}
}
