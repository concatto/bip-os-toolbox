package br.univali.bipost;

public class AssemblyLabel {
	public static final String DEFAULT = "<none>";
	private String name;
	private InstructionGroup group;
	private int address;
	
	public AssemblyLabel() {
		this(DEFAULT, 0);
	}
	
	public AssemblyLabel(String name, int address) {
		this.name = name;
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public InstructionGroup getGroup() {
		return group;
	}
	
	public void setGroup(InstructionGroup group) {
		this.group = group;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AssemblyLabel)) {
			return false;
		}
		
		return ((AssemblyLabel) obj).name.equals(this.name);
	}
}
