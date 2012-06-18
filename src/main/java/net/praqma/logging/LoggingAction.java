package net.praqma.logging;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import hudson.model.Action;

public class LoggingAction implements Action {

	protected transient LoggingStream loggingStream;
	protected transient List<LoggerTarget> targets;
	protected transient LoggingHandler handler;

	public LoggingAction( FileOutputStream out, List<LoggerTarget> targets ) {
		loggingStream = new LoggingStream( out );
		this.targets = targets;
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
}
