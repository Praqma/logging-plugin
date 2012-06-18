package net.praqma.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.model.AbstractProject;
import hudson.model.Job;

public class LoggingJobProperty extends JobProperty<Job<?, ?>> {

	public static final String[] levels = { "all", "finest", "finer", "fine", "config", "info", "warning", "severe" };
	
	private List<LoggerTarget> targets;
	
	private boolean pollLogging = false;

	@DataBoundConstructor
	public LoggingJobProperty( boolean pollLogging ) {
		this.pollLogging = pollLogging;
	}

	private void setTargets( List<LoggerTarget> targets ) {
		this.targets = targets;
	}
	
	public List<LoggerTarget> getTargets() {
		return targets;
	}
	
	public boolean isPollLogging() {
		return pollLogging;
	}
	
	@Extension
	public static class DescriptorImpl extends JobPropertyDescriptor {

		public JobProperty<?> newInstance( StaplerRequest req, JSONObject formData ) throws FormException {
			Object debugObject = formData.get( "debugLog" );

			System.out.println( formData.toString( 2 ) );
			
			if( debugObject != null ) {
				JSONObject debugJSON = (JSONObject) debugObject;
				
				boolean pollLogging = debugJSON.getBoolean( "pollLogging" );

				LoggingJobProperty instance = new LoggingJobProperty( pollLogging );

				List<LoggerTarget> targets = req.bindParametersToList( LoggerTarget.class, "logging.logger." );
				instance.setTargets( targets );
				
				return instance;
			}

			return null;
		}

		@Override
		public String getDisplayName() {
			return "Logging";
		}

		@Override
		public boolean isApplicable( Class<? extends Job> jobType ) {
			return true;
		}

		public String[] getLogLevels() {
			return levels;
		}

		public List<LoggerTarget> getAcceptableLoggerNames( LoggingJobProperty instance ) {
			if( instance == null ) {
				return new ArrayList<LoggerTarget>();
			} else {
				return instance.getTargets();
			}
		}

	}

	public String toString() {
		return "Logging job property, " + targets;
	}

}
