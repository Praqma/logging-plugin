package net.praqma.logging;

import java.io.FileOutputStream;
import java.io.OutputStream;

import hudson.model.Action;

public class LoggingAction implements Action {

	private transient LoggingStream loggingStream;
	private transient String logLevel;

	public LoggingAction( FileOutputStream out, String logLevel ) {
		loggingStream = new LoggingStream( out );
		this.logLevel = logLevel;
	}

	public OutputStream getOut() {
		return loggingStream.getOutputStream();
	}

	public LoggingStream getLoggingStream() {
		return loggingStream;
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
