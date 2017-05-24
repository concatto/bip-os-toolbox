package br.univali.bipost;

public class InstructionGroup {
	private String name;
	private LabelDelimiter[] delimiters;
	private LabelDelimiter currentDelimiter;
	
	public InstructionGroup(String name, LabelDelimiter... delimiters) {
		this.name = name;
		this.delimiters = delimiters;
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public LabelDelimiter[] getDelimiters() {
		return delimiters;
	}
	
	public boolean testStarting(AssemblyLabel label) {
		for (LabelDelimiter delimiter : delimiters) {
			if (delimiter.getStart().equals(label)) {
				currentDelimiter = delimiter;
				return true;
			}
		}
		
		return false;
	}
	
	public boolean testEnding(AssemblyLabel label) {
		if (currentDelimiter != null) {
			if (currentDelimiter.getEnd().equals(label)) {
				currentDelimiter = null;
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof InstructionGroup)) {
			return false;
		}
		
		return ((InstructionGroup) obj).name.equals(this.name);

	}
}
