package net.praqma.logging;

import java.util.logging.Handler;
import java.util.logging.Logger;

import hudson.Extension;
import hudson.model.TaskListener;
import hudson.model.AbstractProject;
import hudson.model.listeners.SCMPollListener;
import hudson.scm.PollingResult;

@Extension
public class LoggingPollingListener extends SCMPollListener {

	@Override
	public void onBeforePolling( AbstractProject<?, ?> project, TaskListener listener ) {
		
		LoggingJobProperty prop = (LoggingJobProperty) project.getProperty( LoggingJobProperty.class );
		System.out.println( "PROP: " + prop );
		System.out.println( "PROP: " + prop.isPollLogging() );
		if( prop != null && prop.isPollLogging() ) {
			try {
				long id = Thread.currentThread().getId();
				LoggingAction action = prop.getLoggingAction( id );
			} catch( Exception e ) {
				listener.getLogger().println( "Failed to instantiate logger: " + e.getMessage() );
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onPollingSuccess( AbstractProject<?, ?> project, TaskListener listener, PollingResult result ) {
		onAfterPolling( project, listener );
	}
	
	@Override
	public void onPollingFailed( AbstractProject<?, ?> project, TaskListener listener, Throwable exception ) {
		onAfterPolling( project, listener );
	}

	public void onAfterPolling( AbstractProject<?, ?> project, TaskListener listener ) {
		
		long id = Thread.currentThread().getId();
		
		LoggingJobProperty prop = (LoggingJobProperty) project.getProperty( LoggingJobProperty.class );
		if( prop != null ) {
			try {
				LoggingAction action = prop.getLoggingAction( id );
				
				if( action != null ) {
					Logger rootLogger = Logger.getLogger( "" );
					
					Handler handler = action.getHandler();
					handler.flush();
					handler.close();

					rootLogger.removeHandler( handler );

					action.getOut().flush();
					action.getOut().close();
				}
			} catch( Exception e ) {
				listener.getLogger().println( "Failed to tear down logger: " + e.getMessage() );
				e.printStackTrace();
			} finally {
				prop.resetPollhandler( id );
			}
		}
		
	}
	
}
