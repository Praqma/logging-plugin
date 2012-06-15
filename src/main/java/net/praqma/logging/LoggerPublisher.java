package net.praqma.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import net.sf.json.JSONObject;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.remoting.Callable;
import hudson.remoting.RemoteOutputStream;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildStep;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.StreamTaskListener;

public class LoggerPublisher extends Recorder {
	
	private static Logger logger = Logger.getLogger( LoggerPublisher.class.getName() );
	
	@DataBoundConstructor
	public LoggerPublisher() {
		
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.BUILD;
	}

	@Override
	public boolean perform( AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener ) throws InterruptedException, IOException {
		
		//FileOutputStream fos = new FileOutputStream( new File( build.getRootDir(), "debug" ) );

		int tid = (int) Thread.currentThread().getId();
		System.out.println( "publisher thread " + tid );
		
		Logger logger = Logger.getLogger( "Wolles.logger" );
		logger.fine( "I'm fine" );
		logger.severe( "I'm severe" );
		
		Logger logger2 = Logger.getLogger( "snade.logger" );
		logger2.fine( "I'm fine2" );
		logger2.severe( "I'm severe2" );
		
		for( Handler h : logger.getHandlers() ) {
			System.out.println( "Handler " + h + ", " + h.getLevel() + ", " + h.getFormatter() );
		}
		
		/*
		TaskListener tl = new StreamTaskListener( new File( build.getRootDir(), "debug2" ) );
		
		FileOutputStream fo = new FileOutputStream(new File( build.getRootDir(), "debug" ));
		LoggingAction action = new LoggingAction( fo );
		build.addAction(action);
		
		Formatter formatterTxt = new SimpleFormatter();
		StreamHandler sh = new StreamHandler( fo, formatterTxt );
		sh.setLevel(Level.FINEST);
		
		Logger rootlogger = Logger.getLogger("");
		rootlogger.addHandler(sh);
		
		Logger logger = Logger.getLogger("wolles.logger.to.com");
		logger.setLevel(Level.INFO);
		
		logger.severe("My log");
		
		logger.info("FROM PUBLISHER LOGGER");
		
		FilePath workspace = build.getWorkspace();
		
		try {
			//workspace.act( new RemoteLogger( tl ) );
			workspace.act( new RemoteTest( build ) );
		} catch( Exception e ) {
			ExceptionUtils.printRootCauseStackTrace( e, listener.getLogger() );
		}
		
		logger.info("STOPPING...");
		
		PrintStream out = new PrintStream( fo );
		out.println( "------------- ENDING ------------------" );
		
		sh.flush();
		sh.close();
		*/
		
		return true;
		
	}

	
	@Extension
	public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

		public DescriptorImpl() {
			super( LoggerPublisher.class );
			load();
		}
		
		@Override
		public LoggerPublisher newInstance( StaplerRequest req, JSONObject data ) {
			return new LoggerPublisher();
		}

		@Override
		public String getDisplayName() {
			return "Logger test";
		}

		@Override
		public boolean isApplicable( Class<? extends AbstractProject> arg0 ) {
			// TODO Auto-generated method stub
			return true;
		}

	}


}
