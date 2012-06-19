package net.praqma.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Logger;

import hudson.Extension;
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

			FileOutputStream fos;
			try {
				File path = new File( project.getRootDir(), "poll-logging" );
				path.mkdir();
				File file = new File( path, "logging" );
				
				fos = new FileOutputStream( file );
				//LoggingProjectAction action = new LoggingProjectAction( fos, prop.getTargets() );
				
				LoggingAction action = null;
				try {
					action = new LoggingAction( fos, prop.getTargets() );
	
					listener.getLogger().println( "PRE POLL Action: " + action );
				} catch( Exception e ) {
					listener.getLogger().println( "Couldn't save: " + e.getMessage() );
					e.printStackTrace();
				}
				
				
				try {
					listener.getLogger().println( "addunf" );
					project.getActions().add( action );
					//project.addAction( action );
					listener.getLogger().println( "Save" );
					project.save();
				} catch( IOException e ) {
					listener.getLogger().println( "Couldn't save: " + e.getMessage() );
					e.printStackTrace();
				}

				listener.getLogger().println( "111111" );
				
				/* Get handler */
				LoggingHandler handler = LoggingUtils.createHandler( fos );
				action.setHandler( handler );
				
				listener.getLogger().println( "22222" );
				
				
				LoggingUtils.addTargetsToHandler( handler, prop.getTargets() );
				
				
				LoggingAction action2 = project.getAction( LoggingAction.class );
				listener.getLogger().println( "Action2: " + action2 );

			} catch( FileNotFoundException e ) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onAfterPolling( AbstractProject<?, ?> project, TaskListener listener ) {
		listener.getLogger().println( "WHOOP! After polling..." );
		
		Logger rootLogger = Logger.getLogger( "" );

		LoggingAction action = project.getAction( LoggingAction.class );
		listener.getLogger().println( "Action: " + action );
		
		if( action != null ) {
			Handler handler = action.getHandler();
			handler.flush();
			handler.close();
			
			rootLogger.removeHandler( handler );
		}
	}
	
}
