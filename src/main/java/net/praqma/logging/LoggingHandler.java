package net.praqma.logging;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class LoggingHandler extends StreamHandler {

	private int threadId;
	private Set<LoggerTarget> targets = new HashSet<LoggerTarget>();
	private OutputStream out;

	public LoggingHandler( OutputStream fos, Formatter formatter ) {
		super( fos, formatter );
		this.out = fos;
		
		this.threadId = (int) Thread.currentThread().getId();
		out = new PrintStream( fos, true );
	}
	
	public OutputStream getOut() { 
		return out;
	}
	
	public void addTarget( String name, String level ) {
		targets.add( new LoggerTarget( name, level ) );
	}
	
	public void addTarget( LoggerTarget target ) {
		targets.add( target );
	}
	
	public void addTarget( Set<LoggerTarget> targets ) {
		targets.addAll( targets );
	}
	
	public void addTargets( List<LoggerTarget> targets ) {
		for( LoggerTarget t : targets ) {
			System.out.println( "Adding " + t + " to " + this );
			addTarget( t );
			
			/* Creating or updating existing loggers */
			Logger logger = LogManager.getLogManager().getLogger( t.getName() );
			if( logger != null ) {
				logger.setLevel( Level.ALL );
			} else {
				Logger nlogger = Logger.getLogger( t.getName() );
				nlogger.setLevel( Level.ALL );
			}
		}
	}

	@Override
	public void publish( LogRecord logRecord ) {
		if( threadId == Thread.currentThread().getId() && checkTargets( logRecord ) ) {
			super.publish( logRecord );
		} else {
			/* No op, not same thread */
		}
	}
	
	private boolean checkTargets( LogRecord lr ) {
		for( LoggerTarget target : targets ) {
			if( checkTarget( target, lr ) ) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean checkTarget( LoggerTarget target, LogRecord lr ) {
		
		if( lr.getLevel().intValue() < target.getLogLevel() ) {
			return false;
		}
		
		if( target.getName() == null || !lr.getLoggerName().startsWith( target.getName() ) ) {
			return false;
		}

		String rest = lr.getLoggerName().substring( target.getName().length() );
		if( rest.length() == 0 || rest.startsWith( "." ) ) {
			
			return true;
		}
		
		return false;
	}
}
