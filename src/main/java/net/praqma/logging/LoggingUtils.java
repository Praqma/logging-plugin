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
	
}
