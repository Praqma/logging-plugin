package net.praqma.logging;

import org.kohsuke.stapler.DataBoundConstructor;

public class LoggerTarget {
	
	private String level;
	private String name;
	
	@DataBoundConstructor
	public LoggerTarget() {
		
	}

	public String getLevel() {
		return level;
	}

	public void setLevel( String level ) {
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}
	
	public String toString() {
		return name + ", " + level;
	}
}
