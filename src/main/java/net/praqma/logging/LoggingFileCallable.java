package net.praqma.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import hudson.FilePath.FileCallable;
import hudson.model.Item;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Actionable;
import hudson.remoting.RemoteOutputStream;
import hudson.remoting.VirtualChannel;

public abstract class LoggingFileCallable<T> implements FileCallable<T> {

	protected LoggingStream lstream;
	private List<LoggerTarget> targets;
	private boolean remote = false;

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
			//remote = build.getWorkspace().isRemote();
			
			action.getHandler().flush();
		}
	}
	
	private void initialize( AbstractProject<?, ?> project ) {
		LoggingJobProperty prop = (LoggingJobProperty) project.getProperty( LoggingJobProperty.class );
		if( prop != null ) {
			try {
				LoggingAction action = prop.getLoggingAction();
				if( action != null ) {
					lstream = action.getLoggingStream();
					targets = action.getTargets();
				}
			} catch( Exception e ) {
				
			}
		}
	}

	
	/*
	private LoggingFileCallable( AbstractBuild<?, ?> build ) {
		LoggingAction action = build.getAction( LoggingAction.class );
		if( action != null ) {
			lstream = action.getLoggingStream();
			targets = action.getTargets();
			remote = build.getWorkspace().isRemote();
			
			action.getHandler().flush();
		}
	}
	*/


	public abstract T perform( File workspace, VirtualChannel channel ) throws IOException, InterruptedException;

	@Override
	public T invoke( File workspace, VirtualChannel channel ) throws IOException, InterruptedException {

		/* Setup logger */
		LoggingHandler handler = LoggingUtils.createHandler( lstream.getOutputStream() );
		LoggingUtils.addTargetsToHandler( handler, targets );

		T result = null;
		try {
			result = perform( workspace, channel );
		} finally {
			/* Tear down logger */
			LoggingUtils.removeHandler( handler );
			
			new PrintStream( lstream.getOutputStream() ).println( "STREAM: " + lstream.getOutputStream() );
			new PrintStream( lstream.getOutputStream() ).println( "REMOTE: " + isRemote() );
			
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
