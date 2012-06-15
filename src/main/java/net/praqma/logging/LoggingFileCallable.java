package net.praqma.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import hudson.FilePath.FileCallable;
import hudson.model.AbstractBuild;
import hudson.remoting.VirtualChannel;

public abstract class LoggingFileCallable<T> implements FileCallable<T> {

	protected LoggingStream lstream;
	
	public LoggingFileCallable( AbstractBuild<?, ?> build ) {
		LoggingAction action = build.getAction(LoggingAction.class);
		if( action != null ) {
			lstream = action.getLoggingStream();
		}
	}
	
	public abstract T perform( File workspace, VirtualChannel channel ) throws IOException,	InterruptedException;
	
	@Override
	public T invoke(File arg0, VirtualChannel arg1) throws IOException,
			InterruptedException {
		
		/* Setup logger */
		Formatter formatterTxt = new SimpleFormatter();
		StreamHandler sh = new StreamHandler( lstream.getOutputStream(), formatterTxt );
		sh.setLevel(Level.FINEST);
		
		Logger rootlogger = Logger.getLogger("");
		rootlogger.addHandler(sh);

		Logger logger = Logger.getLogger("wolles.logger.com");
		logger.setLevel(Level.INFO);
		
		logger.severe("My log");
		
		PrintStream out = new PrintStream( lstream.getOutputStream() );
		out.println( "FROM INVOKE" );
		out.println( "OUT IS " + lstream.getOutputStream().getClass() + " -- " + lstream.getOutputStream() );

		out.println( "GLOBAL: " + rootlogger.getName() + ", " + rootlogger.getLevel() + " - " + rootlogger.getParent() );
				
		for( Handler h : logger.getHandlers() ) {
			out.println( "Handler " + h + ", " + h.getLevel() + ", " + h.getFormatter() );
		}
		
		logger.info("----> <----");
		
		T result = null;
		try {
			result = perform(arg0, arg1);
		} finally {
			/* Teardown logger */

			rootlogger.removeHandler(sh);
			/* Do this on remotes?! */
			//sh.flush();
			//sh.close();
			
			
			out.println("CLOSING UP");
			//lstream.getOutputStream().flush();
			//lstream.getOutputStream().close();
		}
		
		return result;
		
		
	}

}
