package net.praqma.logging;

import java.util.ArrayList;
import java.util.List;
import javaposse.jobdsl.dsl.Context;
import static javaposse.jobdsl.dsl.Preconditions.checkArgument;
import static javaposse.jobdsl.plugin.ContextExtensionPoint.executeInContext;

class LoggingJobDslContext implements Context {

    boolean pollLogging;
    int pruneDays;
    List<LoggingTarget> targets = new ArrayList<LoggingTarget>();

    public void pollLogging() {
        pollLogging = true;
    }

    public void pollLogging(boolean value) {
        pollLogging = value;
    }

    public void pruneDays(int value) {
        checkArgument(value >= 0 && value <= 30, "pruneDays should be between 0 and 30");
        pruneDays = value;
    }
    
    public void target(Runnable closure){
        LoggingTargetsJobDslContext context = new LoggingTargetsJobDslContext();
        executeInContext(closure, context);
        
        LoggingTarget target = new LoggingTarget(context.name, context.level);
        targets.add(target);
    }
}
