package net.praqma.logging;

import hudson.model.ProminentProjectAction;

public class LoggingProjectAction implements ProminentProjectAction {

    @Override
    public String getIconFileName() {
        return "/plugin/logging/images/notebook.png";
    }

    @Override
    public String getDisplayName() {
        return "Poll Logging";
    }

    @Override
    public String getUrlName() {
        return "poll-logging";
    }
}
