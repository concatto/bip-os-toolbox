package br.univali.bipost;

public class LabelDelimiter {
	private AssemblyLabel start;
	private AssemblyLabel end;
	
	public LabelDelimiter(AssemblyLabel start, AssemblyLabel end) {
		this.start = start;
		this.end = end;
	}
	
	public AssemblyLabel getStart() {
		return start;
	}
	
	public AssemblyLabel getEnd() {
		return end;
	}
	
	public void setStart(AssemblyLabel start) {
		this.start = start;
	}
	
	public void setEnd(AssemblyLabel end) {
		this.end = end;
	}
}
