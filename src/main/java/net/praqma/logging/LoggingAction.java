package net.praqma.logging;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import hudson.model.Action;

public class LoggingAction implements Action {

	protected transient LoggingStream loggingStream;
	protected transient List<LoggerTarget> targets;
	protected transient LoggingHandler handler;
	
	protected String mystring;

	public LoggingAction( FileOutputStream out, List<LoggerTarget> targets ) {
		loggingStream = new LoggingStream( out );
		this.targets = targets;
		
		this.mystring = "NEJ NJE";
	}
	
	public LoggingAction( LoggingHandler handler, List<LoggerTarget> targets ) {
		loggingStream = new LoggingStream( handler.getOut() );
		this.targets = targets;
		this.handler = handler;
	}

	public OutputStream getOut() {
		return loggingStream.getOutputStream();
	}

	public LoggingStream getLoggingStream() {
		return loggingStream;
	}
	
	public List<LoggerTarget> getTargets() {
		return targets;
	}
	
	public LoggingHandler getHandler() {
		return handler;
	}

	public void setHandler( LoggingHandler handler ) {
		this.handler = handler;
	}

	@Override
	public String getDisplayName() {
		return null;
	}

	@Override
	public String getIconFileName() {
		return null;
	}

	@Override
	public String getUrlName() {
		return null;
	}
	
	public String toString() {
		return "Targets: " + targets + ", " + handler + " - " + mystring;
	}
}
