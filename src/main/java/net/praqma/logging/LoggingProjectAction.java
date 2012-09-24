package net.praqma.logging;

import hudson.model.Job;
import hudson.model.ProminentProjectAction;
import org.apache.commons.io.FileUtils;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class LoggingProjectAction implements ProminentProjectAction {

    private Job<?, ?> job;

    public LoggingProjectAction( Job<?, ?> job ) {
        this.job = job;
    }

    @Override
    public String getIconFileName() {
        return "/images/48x48/notepad.png";
    }

    @Override
    public String getDisplayName() {
        return "Poll Logs";
    }

    @Override
    public String getUrlName() {
        return "poll-logs";
    }

    public List<Logging.PollLoggingFile> getLogs() {
        List<Logging.PollLoggingFile> list = Logging.getPollLogs( new File( job.getRootDir(), Logging.POLLLOGPATH ) );
        Collections.sort( list, new Logging.ComparePollLogs() );
        return list;
    }

    public void doGetPollLog( StaplerRequest req, StaplerResponse rsp ) throws IOException, ServletException {
        String logname = req.getParameter( "log" );

        File logPath = new File( job.getRootDir(), Logging.POLLLOGPATH );
        File logFile = new File( logPath, logname );

        if( logFile.exists() ) {
            Calendar t = Calendar.getInstance();
            t.setTimeInMillis(logFile.lastModified());
            rsp.serveFile( req, FileUtils.openInputStream( logFile ), t.getTimeInMillis(), logFile.getTotalSpace(), logFile.getName() );
        } else {
            rsp.sendError( HttpServletResponse.SC_NOT_FOUND );
        }
    }
}
