package net.praqma.logging;

import java.io.Serializable;
import java.util.logging.Level;

import org.kohsuke.stapler.DataBoundConstructor;


public class LoggerTarget implements Serializable {
	
	private String level;
	private String name;
	private int logLevel;
	
	public LoggerTarget() {
		System.out.println( "Whoops, wrong constructor" );
	}
	
	@DataBoundConstructor
	public LoggerTarget( String name, String level ) {
		System.out.println( "Yay, correct constructor" );
		this.name = name;
		this.level = level;
		
		this.logLevel = Level.parse( level ).intValue();
	}

	public String getLevel() {
		return level;
	}

	public void setLevel( String level ) {
		this.level = level;
		this.logLevel = Level.parse( level ).intValue();
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
	
	public int getLogLevel() {
		return logLevel;
	}
}
