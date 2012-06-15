package net.praqma.logging;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
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

	public LoggingHandler( OutputStream fos, Formatter formatter ) {
		super( fos, formatter );
		
		this.threadId = (int) Thread.currentThread().getId();
		System.out.println( "Locking handler to " + Thread.currentThread().getId() );
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

	@Override
	public void publish( LogRecord logRecord ) {
		if( threadId == Thread.currentThread().getId() && checkTargets( logRecord ) ) {
			System.out.println( "Adding log for " + logRecord.getLoggerName() );
			super.publish( logRecord );
		} else {
			/* No op, not same thread */
			System.out.println( "NOT THE SAME THREAD" );
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
		System.out.println( "Target: " + target + ", Record: " + lr.getLoggerName() + ", " + lr.getLevel() );
		System.out.println( "Target: " + target.getLogLevel() + ", Record: " + lr.getLevel().intValue() );
		
		if( lr.getLevel().intValue() < target.getLogLevel() ) {
			return false;
		}
		
		if( target.getName() == null || !lr.getLoggerName().startsWith( target.getName() ) ) {
			return false;
		}

		String rest = lr.getLoggerName().substring( target.getName().length() );
		if( rest.length() == 0 || rest.startsWith( "." ) ) {
			System.out.println( "Adding log" );
			
			return true;
		}
		
		return false;
	}
}
