package net.praqma.logging;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class LoggingHandler extends StreamHandler {

	private int threadId;
	
	public LoggingHandler( int threadId ) {
		this.threadId = threadId;
	}
	
	public LoggingHandler( OutputStream fos, Formatter formatter ) {
		super( fos, formatter );
		
		this.threadId = (int) Thread.currentThread().getId();
		System.out.println( "Locking handler to " + Thread.currentThread().getId() );
	}

	@Override
	public void publish( LogRecord logRecord ) {
		if( threadId == Thread.currentThread().getId() ) {
			super.publish( logRecord );
		} else {
			/* No op, not same thread */
			System.out.println( "NOT THE SAME THREAD" );
		}
	}
}
