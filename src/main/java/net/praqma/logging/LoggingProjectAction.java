package net.praqma.logging;

import java.io.FileOutputStream;
import java.util.List;

import hudson.model.ProminentProjectAction;

public class LoggingProjectAction extends LoggingAction implements ProminentProjectAction {

	public LoggingProjectAction( FileOutputStream out, List<LoggerTarget> targets ) {
		super( out, targets );
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
