package net.praqma.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import hudson.Extension;
import hudson.Launcher;
import hudson.matrix.MatrixRun;
import hudson.model.BuildListener;
import hudson.model.Environment;
import hudson.model.TaskListener;
import hudson.model.AbstractBuild;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.listeners.RunListener;

@Extension
public class LoggingRunListener extends RunListener<Run> {

	public LoggingRunListener() {
		super( Run.class );
	}
	
	private List<Handler> handlers = new ArrayList<Handler>();


	@Override
	public void onStarted( Run r, TaskListener listener ) {

		if( r == null ) {
			throw new IllegalArgumentException( "A build object must be set." );
		}

		Job job;
		if( r instanceof MatrixRun ) {
			job = ( (MatrixRun) r ).getParentBuild().getParent();
		} else {
			job = r.getParent();
		}

		LoggingJobProperty prop = (LoggingJobProperty) job.getProperty( LoggingJobProperty.class );
		if( prop != null ) {
			System.out.println( "Adding logging handler to " + r + " with " + prop );

			FileOutputStream fos;
			try {
				File file = new File( r.getRootDir(), "debug-take-two" );
				fos = new FileOutputStream( file );
				System.out.println( "File " + file );
				System.out.println( "FOS " + fos );
				LoggingAction action = new LoggingAction( fos, prop.getLogLevel() );

				r.addAction( action );

				/* Setup handler */
				Formatter formatter = new SimpleFormatter();
				Handler sh = new LoggingHandler( fos, formatter );
				//Handler sh = new StreamHandler( fos, formatter );
				
				Level level = Level.parse( prop.getLogLevel() );
				sh.setLevel( level );
				//sh.setLevel( Level.FINEST );
				
				Logger rootLogger = Logger.getLogger( "" );
				rootLogger.addHandler( sh );
								
				
				/*
				logger.finest( "FROM ROOT LOGGER" );
				logger.finer( "FROM ROOT LOGGER" );
				logger.fine( "FROM ROOT LOGGER" );
				logger.config( "FROM ROOT LOGGER" );
				logger.info( "FROM ROOT LOGGER" );
				logger.warning( "FROM ROOT LOGGER" );
				logger.severe( "FROM ROOT LOGGER" );
				*/
				
				/*
				for( Handler h : rootLogger.getHandlers() ) {
					System.out.println( "Handler " + h + ", " + h.getLevel() + ", " + h.getFormatter() );
				}
				*/
				
				handler = sh;

			} catch( FileNotFoundException e ) {
				e.printStackTrace();
			}

		}
		
	}
	
	@Override
	public void onCompleted( Run r, TaskListener listener ) {
		System.out.println( "Removing handler from "+ r );
		handler.flush();
		handler.close();
		Logger rootLogger = Logger.getLogger( "" );
		rootLogger.removeHandler( handler );
	}
	

	/**
	 * Setup logging handler, add to the root logger and to the list
	 * @param name
	 * @param level
	 * @param action
	 */
	public void createHandler( String name, String level, LoggingAction action ) {

		Formatter formatter = new SimpleFormatter();
		Handler sh = new LoggingHandler( action.getOut(), formatter );

		Level loglevel = Level.parse( level );
		sh.setLevel( loglevel );

		Logger rootLogger = Logger.getLogger( "" );
		rootLogger.addHandler( sh );

		handlers.add( sh );

	}
	
	
	public static class LoggerNameFilter implements Filter {

		private Set<String> acceptableNames = new HashSet<String>();
		
		public LoggerNameFilter( String acceptableName ) {
			this.acceptableNames.add( acceptableName );
		}
		
		public LoggerNameFilter( String[] acceptableNames ) {
			this.acceptableNames.addAll( Arrays.asList( acceptableNames ) );
		}
		
		public LoggerNameFilter( List<String> acceptableNames ) {
			this.acceptableNames.addAll( acceptableNames );
		}
		
		@Override
		public boolean isLoggable( LogRecord lr ) {
			System.out.println( "Adding log for " + lr.getLoggerName() );
			if( acceptableNames.contains( lr.getLoggerName() ) ) {
				return true;
			} else {
				return false;
			}
		}
		
	}
	
	
	public static class MyFilter implements Filter {

		private int threadId;
		
		public MyFilter( int threadId ) {
			this.threadId = threadId;
		}
		
		@Override
		public boolean isLoggable( LogRecord lr ) {
			System.out.println( "Comparing " + lr.getThreadID() + " and " + threadId );
			if( lr.getThreadID() == threadId ) {
				return true;
			} else {
				return false;
			}
		}
		
	}
}
