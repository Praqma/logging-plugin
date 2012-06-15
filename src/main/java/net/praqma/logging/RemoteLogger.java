package net.praqma.logging;

import hudson.FilePath.FileCallable;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.remoting.RemoteOutputStream;
import hudson.remoting.VirtualChannel;
import hudson.util.StreamTaskListener;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import org.apache.commons.lang.exception.ExceptionUtils;

public class RemoteLogger implements FileCallable<Boolean> {
	
	private transient Logger logger;
	
	TaskListener listener;
	
	public RemoteLogger( TaskListener listener ) {
		this.listener = listener;
	}

	@Override
	public Boolean invoke( File arg0, VirtualChannel arg1 ) throws IOException, InterruptedException {
		logger = Logger.getLogger( RemoteLogger.class.getName() );
		
		logger.severe( "Whoa!" );
		
		
		try {
			StreamTaskListener stl = (StreamTaskListener)listener;
			
			listener.getLogger().println( "Wolle was here" );

		} catch( Exception e ) {
			ExceptionUtils.printRootCauseStackTrace( e, listener.getLogger() );
		}
		
		return true;
	}
}
