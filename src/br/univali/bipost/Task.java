package br.univali.bipost;

public class Task implements Comparable<Task> {
	public static final String BEGIN_TOKEN = "begin";
	public static final String END_TOKEN = "end";
	public static final int MIN_PRIORITY = 1;
	public static final int MAX_PRIORITY = 5;
	
	private String name;
	private String sourceCode;
	private int priority;
	
	public Task() {
		
	}

	public Task(String name, String sourceCode, int priority) {
		setName(name);
		this.sourceCode = sourceCode;
		this.priority = priority;
	}
	
	public Task(String name, int priority) {
		this(name, "${begin}\n\n  JMP OS_TSK_END\n${end}", priority);
	}

	public String getName() {
		return name;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public int getPriority() {
		return priority;
	}

	public void setName(String name) {
		this.name = name.toUpperCase().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9\\-_]+", "");
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public int compareTo(Task other) {
		return Integer.compare(this.priority, other.priority);
	}
	
	@Override
	public String toString() {
		return String.format("%s (P=%d)", this.name, this.priority);
	}
}
