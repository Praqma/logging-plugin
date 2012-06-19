package net.praqma.logging;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import hudson.FilePath.FileCallable;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Actionable;
import hudson.remoting.RemoteOutputStream;
import hudson.remoting.VirtualChannel;

public abstract class LoggingFileCallable<T> implements FileCallable<T> {

	protected LoggingStream lstream;
	private List<LoggingTarget> targets;

	public LoggingFileCallable( Actionable a ) {
		if( a instanceof AbstractBuild ) {
			initialize( (AbstractBuild)a );
		} else if( a instanceof AbstractProject ) {
			initialize( (AbstractProject)a );
		}
	}
	
	private void initialize( AbstractBuild<?, ?> build ) {
		LoggingAction action = build.getAction( LoggingAction.class );
		if( action != null ) {
			lstream = action.getLoggingStream();
			targets = action.getTargets();
			
			action.getHandler().flush();
		}
	}
	
	private void initialize( AbstractProject<?, ?> project ) {
		LoggingJobProperty prop = (LoggingJobProperty) project.getProperty( LoggingJobProperty.class );
		if( prop != null ) {
			try {
				LoggingAction action = prop.getLoggingAction( Thread.currentThread().getId() );
				if( action != null ) {
					lstream = action.getLoggingStream();
					targets = action.getTargets();
				}
			} catch( Exception e ) {
				
			}
		}
	}

	public abstract T perform( File workspace, VirtualChannel channel ) throws IOException, InterruptedException;

	@Override
	public T invoke( File workspace, VirtualChannel channel ) throws IOException, InterruptedException {

		/* Setup logger */
		LoggingHandler handler = LoggingUtils.createHandler( lstream.getOutputStream() );
		handler.addTargets( targets );

		T result = null;
		try {
			result = perform( workspace, channel );
		} finally {
			/* Tear down logger */
			LoggingUtils.removeHandler( handler );
			
			new PrintStream( lstream.getOutputStream() ).println( "STREAM: " + lstream.getOutputStream() );
			new PrintStream( lstream.getOutputStream() ).println( "REMOTE: " + isRemote() );
			new PrintStream( lstream.getOutputStream() ).println( "WS: " + workspace.getAbsoluteFile() );
			
			/* If remote flush and close handler */
			if( isRemote() ) {
				try {
					handler.flush();
					handler.close();
				} catch( Exception e ) {
					/* Unable to close handler */
				}
			}
		}

		return result;

	}
	
	private boolean isRemote() {
		return lstream.getOutputStream() instanceof RemoteOutputStream;
	}

}
