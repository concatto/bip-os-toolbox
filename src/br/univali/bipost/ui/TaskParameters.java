package br.univali.bipost.ui;

public class TaskParameters {
	private String name;
	private int priority;
	
	public TaskParameters(String name, int priority) {
		this.name = name;
		this.priority = priority;
	}
	
	public String getName() {
		return name;
	}
	
	public int getPriority() {
		return priority;
	}
}