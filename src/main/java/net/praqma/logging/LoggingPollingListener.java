package net.praqma.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.TaskListener;
import hudson.model.AbstractProject;
import hudson.model.listeners.SCMPollListener;

@Extension
public class LoggingPollingListener extends SCMPollListener {

	@Override
	public void onBeforePolling( AbstractProject<?, ?> project, TaskListener listener ) {
		listener.getLogger().println( "WHOOP! Before1111 polling..." );
		
		LoggingJobProperty prop = (LoggingJobProperty) project.getProperty( LoggingJobProperty.class );
		if( prop != null ) {
			listener.getLogger().println( "WHOOP! Property defined!" );

			//List<? extends Action> actions = (List<? extends Action>) prop.getJobActions( project );
			try {
				LoggingAction action = prop.getLoggingAction();
				
				if( action != null ) {
					
					/* Get handler */
					listener.getLogger().println( "Action1: " + action );
					
					listener.getLogger().println( "Thread1: " + Thread.currentThread().getId() );
					listener.getLogger().println( "Thread1: " + Thread.currentThread().getName() );
				}
			} catch( Exception e ) {
				
			}
		}
	}
	
	@Override
	public void onAfterPolling( AbstractProject<?, ?> project, TaskListener listener ) {
		listener.getLogger().println( "WHOOP! After polling..." );
		
		
		LoggingJobProperty prop = (LoggingJobProperty) project.getProperty( LoggingJobProperty.class );
		if( prop != null ) {
			try {
				LoggingAction action = prop.getLoggingAction();
				
				if( action != null ) {
					Logger rootLogger = Logger.getLogger( "" );
					listener.getLogger().println( "Thread3: " + Thread.currentThread().getId() );
					listener.getLogger().println( "Thread3: " + Thread.currentThread().getName() );
					
					Handler handler = action.getHandler();
					listener.getLogger().println( "Handler: " + handler );
					handler.flush();
					handler.close();
					listener.getLogger().println( "Handler closed and flushed" );
					rootLogger.removeHandler( handler );
					listener.getLogger().println( "Handler removed" );
					action.getOut().flush();
					action.getOut().close();
					listener.getLogger().println( "ENDING" );
				}
			} catch( Exception e ) {
				
			} finally {
				prop.resetPollhandler();
			}
		}
		
		
		/*
		Logger rootLogger = Logger.getLogger( "" );

		LoggingAction action = project.getAction( LoggingAction.class );
		listener.getLogger().println( "Action: " + action );
		
		if( action != null ) {
			
			listener.getLogger().println( "Thread3: " + Thread.currentThread().getId() );
			listener.getLogger().println( "Thread3: " + Thread.currentThread().getName() );
			
			Handler handler = action.getHandler();
			listener.getLogger().println( "Handler: " + handler );
			handler.flush();
			handler.close();
			listener.getLogger().println( "Handler closed and flushed" );
			rootLogger.removeHandler( handler );
			listener.getLogger().println( "Handler removed" );

		}
		*/
	}
	
}
