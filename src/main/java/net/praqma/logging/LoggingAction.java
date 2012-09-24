package net.praqma.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Run;
import org.apache.commons.io.FileUtils;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

public class LoggingAction implements Action {

	protected transient LoggingStream loggingStream;
	protected transient List<LoggingTarget> targets;
	protected transient LoggingHandler handler;
    protected transient Run run;

    public LoggingAction( Run run, FileOutputStream out, List<LoggingTarget> targets ) {
		loggingStream = new LoggingStream( out );
		this.targets = targets;
        this.run = run;
	}
	
	public LoggingAction( Run run, LoggingHandler handler, List<LoggingTarget> targets ) {
		loggingStream = new LoggingStream( handler.getOut() );
		this.targets = targets;
		this.handler = handler;
        this.run = run;
	}

	public OutputStream getOut() {
		return loggingStream.getOutputStream();
	}

	public LoggingStream getLoggingStream() {
		return loggingStream;
	}
	
	public List<LoggingTarget> getTargets() {
		return targets;
	}
	
	public LoggingHandler getHandler() {
		return handler;
	}

	public void setHandler( LoggingHandler handler ) {
		this.handler = handler;
	}

    public void doIndex( StaplerRequest req, StaplerResponse rsp ) throws IOException, ServletException {
        if( run != null ) {
            File dm = new File( run.getRootDir(), Logging.LOGFILENAME );
            Calendar t = run.getTimestamp();
            if( dm.exists() ) {
                rsp.serveFile( req, FileUtils.openInputStream( dm ), t.getTimeInMillis(), dm.getTotalSpace(), Logging.LOGFILENAME );
            } else {
                rsp.sendError( HttpServletResponse.SC_NOT_FOUND );
            }
        } else {
            rsp.sendError( HttpServletResponse.SC_NOT_FOUND );
        }
    }

	@Override
	public String getDisplayName() {
		return "Logging";
	}

	@Override
	public String getIconFileName() {
        return "/images/48x48/notepad.png";
	}

	@Override
	public String getUrlName() {
		return "logging";
	}
	
	public String toString() {
		return "Targets: " + targets + ", " + handler;
	}
}
