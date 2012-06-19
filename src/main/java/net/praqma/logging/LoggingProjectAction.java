package net.praqma.logging;

import java.io.FileOutputStream;
import java.util.List;

import hudson.model.ProminentProjectAction;

public class LoggingProjectAction extends LoggingAction implements ProminentProjectAction {

	public LoggingProjectAction( FileOutputStream out, List<LoggerTarget> targets, String t ) {
		super( out, targets, t );
	}

	@Override
	public String getIconFileName() {
		return null;
	}

	@Override
	public String getDisplayName() {
		return null;
	} 

	@Override
	public String getUrlName() {
		return null;
	}

}
