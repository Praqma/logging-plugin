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
				File file = new File( r.getRootDir(), "debug-take-four" );
				fos = new FileOutputStream( file );
				LoggingAction action = new LoggingAction( fos, prop.getTargets() );

				r.addAction( action );

				/* Get handler */
				LoggingHandler handler = LoggingUtils.createHandler( fos );
				action.setHandler( handler );
				
				LoggingUtils.addTargetsToHandler( handler, prop.getTargets() );

			} catch( FileNotFoundException e ) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void onCompleted( Run r, TaskListener listener ) {
		Logger rootLogger = Logger.getLogger( "" );

		LoggingAction action = r.getAction( LoggingAction.class );
		
		if( action != null ) {
			Handler handler = action.getHandler();
			handler.flush();
			handler.close();
			
			rootLogger.removeHandler( handler );
		}
	}

}
