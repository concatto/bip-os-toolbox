package br.univali.bipost;

public class SourceLine {
	private String text;
	private AssemblyLabel label;
	private String instruction;
	
	public SourceLine(String text, AssemblyLabel label) {
		this.text = text;
		this.label = label;
		
		int index = text.indexOf("#");
		if (index < 0) {
			this.instruction = text.trim();
		} else {
			this.instruction = text.substring(0, index).trim();
		}
	}
	
	public String getText() {
		return text;
	}
	
	public AssemblyLabel getLabel() {
		return label;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setLabel(AssemblyLabel label) {
		this.label = label;
	}
	
	public String getInstruction() {
		return instruction;
	}
}
