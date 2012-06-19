package net.praqma.logging;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class LoggingUtils {

	/**
	 * Setup logging handler and add to the root logger
	 * 
	 * @param name
	 * @param level
	 * @param action
	 */
	public static LoggingHandler createHandler( OutputStream fos ) {

		Formatter formatter = new SimpleFormatter();
		LoggingHandler sh = new LoggingHandler( fos, formatter );

		sh.setLevel( Level.ALL );

		Logger rootLogger = Logger.getLogger( "" );
		rootLogger.addHandler( sh );

		return sh;
	}
	
	/**
	 * Remove the given handler from the root logger
	 * @param handler
	 */
	public static void removeHandler( Handler handler ) {
		Logger rootLogger = Logger.getLogger( "" );
		rootLogger.removeHandler( handler );
	}
	
	/**
	 * Add the targets to the handler. Also create or update the target's logger to ALL.
	 * @param handler
	 * @param targets
	 */
	public static void addTargetsToHandler( LoggingHandler handler, List<LoggerTarget> targets ) {
		for( LoggerTarget t : targets ) {
			System.out.println( "Adding " + t + " to " + handler );
			handler.addTarget( t );
			
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
}
