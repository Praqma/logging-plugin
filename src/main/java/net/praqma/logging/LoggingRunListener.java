package net.praqma.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
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

	private LoggingHandler handler;

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
				LoggingAction action = new LoggingAction( fos, prop.getTargets() );

				r.addAction( action );

				/* Get handler */
				handler = createHandler( fos );
				
				for( LoggerTarget t : prop.getTargets() ) {
					handler.addTarget( t );
					
					/* Creating or updating existing loggers */
					Logger logger = LogManager.getLogManager().getLogger( t.getName() );
					if( logger != null ) {
						System.out.println( "EXISTING Logger: " + logger.getName() );
						logger.setLevel( Level.ALL );
					} else {
						Logger nlogger = Logger.getLogger( t.getName() );
						System.out.println( "NEW Logger: " + nlogger.getName() );
						nlogger.setLevel( Level.ALL );
					}
				}

			} catch( FileNotFoundException e ) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void onCompleted( Run r, TaskListener listener ) {
		Logger rootLogger = Logger.getLogger( "" );

		System.out.println( "Removing handler from " + r );
		handler.flush();
		handler.close();
		rootLogger.removeHandler( handler );

	}

	/**
	 * Setup logging handler, add to the root logger and to the list
	 * 
	 * @param name
	 * @param level
	 * @param action
	 */
	public LoggingHandler createHandler( FileOutputStream fos ) {
		//System.out.println( "Creating handler " + name + ", " + level );

		Formatter formatter = new SimpleFormatter();
		LoggingHandler sh = new LoggingHandler( fos, formatter );

		//Level loglevel = Level.parse( level );
		//sh.setLevel( loglevel );
		sh.setLevel( Level.ALL );

		//sh.setFilter( new LoggerNameFilter( name ) );

		Logger rootLogger = Logger.getLogger( "" );
		rootLogger.addHandler( sh );
		//rootLogger.setLevel( Level.ALL );

		return sh;
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

			boolean result = false;
			
			for( String name : acceptableNames ) {
				if( name == null || !lr.getLoggerName().startsWith( name ) ) {
					continue;
				}

				String rest = lr.getLoggerName().substring( name.length() );
				if( rest.length() == 0 || rest.startsWith( "." ) ) {
					System.out.println( "Adding log for " + lr.getLoggerName() );
					return true;
				}
			}
			
			System.out.println( "NOT Adding log for " + lr.getLoggerName() );
			return result;
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
