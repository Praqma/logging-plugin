package net.praqma.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.scm.ChangeLogParser;
import hudson.scm.PollingResult;
import hudson.scm.RepositoryBrowser;
import hudson.scm.SCMDescriptor;
import hudson.scm.SCMRevisionState;
import hudson.scm.SCM;

public class LoggingSCM extends SCM {
	
	private static Logger logger = Logger.getLogger( LoggingSCM.class.getName() );

	@DataBoundConstructor
	public LoggingSCM() {
		
	}
	
	@Override
	public SCMRevisionState calcRevisionsFromBuild( AbstractBuild<?, ?> build, Launcher launcher, TaskListener listener ) throws IOException, InterruptedException {
		SCMRevisionState scmRS = null;
		scmRS = new SCMRevisionState() {};
		return scmRS;
	}

	@Override
	protected PollingResult compareRemoteRevisionWith( AbstractProject<?, ?> project, Launcher launcher, FilePath workspace, TaskListener listener, SCMRevisionState baseline ) throws IOException, InterruptedException {
		
		logger.finer( "I am a fine message" );
		logger.warning( "I am a warning message" );
		logger.severe( "I am a severe message" );
		
		listener.getLogger().println( "Thread2: " + Thread.currentThread().getId() );
		listener.getLogger().println( "Thread2: " + Thread.currentThread().getName() );
		
		listener.getLogger().println( "Workspace: " + workspace.isRemote() );
		listener.getLogger().println( "Workspace: " + workspace.getRemote() );
		listener.getLogger().println( "Workspace: " + workspace.toURI() );
		
		try {
			workspace.act( new RemoteTest( project ) );
		} catch( Exception e ) {
			ExceptionUtils.printRootCauseStackTrace( e, listener.getLogger() );
		}
		
		//return PollingResult.BUILD_NOW;
		return PollingResult.NO_CHANGES;
	}

	@Override
	public boolean checkout( AbstractBuild<?, ?> build, Launcher launcher, FilePath workspace, BuildListener listener, File changelogFile ) throws IOException, InterruptedException {
		return true;
	}

	@Override
	public ChangeLogParser createChangeLogParser() {
		return null;
	}
	
	@Extension
	public static class LoggingSCMDescriptor extends SCMDescriptor<LoggingSCM> {

		public LoggingSCMDescriptor() {
			super( LoggingSCM.class, null );
		}
		
		protected LoggingSCMDescriptor( Class<? extends RepositoryBrowser> repositoryBrowser ) {
			super( repositoryBrowser );
		}
		
		@Override
		public LoggingSCM newInstance( StaplerRequest req, JSONObject formData ) throws FormException {
			LoggingSCM instance = req.bindJSON( LoggingSCM.class, formData );
			return instance;
		}

		@Override
		public String getDisplayName() {
			return "Logging SCM";
		}
		
	}

}
