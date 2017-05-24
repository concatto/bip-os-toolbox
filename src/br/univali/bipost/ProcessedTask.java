package br.univali.bipost;

public class ProcessedTask extends Task {
	private String beginLabel;
	private String endLabel;
	
	public ProcessedTask(String name, String sourceCode, int priority, String beginLabel, String endLabel) {
		super(name, sourceCode, priority);
		this.beginLabel = beginLabel;
		this.endLabel = endLabel;
	}

	public String getBeginLabel() {
		return beginLabel;
	}
	
	public String getEndLabel() {
		return endLabel;
	}
	
	public void setBeginLabel(String beginLabel) {
		this.beginLabel = beginLabel;
	}
	
	public void setEndLabel(String endLabel) {
		this.endLabel = endLabel;
	}
}
