package net.praqma.logging;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Actionable;
import hudson.remoting.DelegatingCallable;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.List;

@Extension
public class LoggingCallableIntercepter<T> extends FilePath.FileCallableWrapperFactory implements Serializable {

    private static ThreadLocal<Actionable> actionables = new ThreadLocal<Actionable>();

    public static synchronized void setActionable( Actionable a ) {
        actionables.set( a );
    }

    @Override
    public <T> DelegatingCallable<T, IOException> wrap( DelegatingCallable<T, IOException> callable ) {
        Actionable a = actionables.get();
        if( a != null ) {
            return new LoggingWrapper( callable );
        } else {
            return callable;
        }
    }


    public class LoggingWrapper<T1> extends FilePath.AbstractInterceptorCallableWrapper<T1> {
        private LoggingStream lstream;
        private List<LoggingTarget> targets;
        private long threadId;

        private LoggingHandler handler;

        public LoggingWrapper( DelegatingCallable<T1, IOException> callable ) {
            super( callable );

            Actionable a = actionables.get();

            if( a != null ) {
                threadId = Thread.currentThread().getId();
                if( a instanceof AbstractBuild ) {
                    initialize( (AbstractBuild) a );
                } else if( a instanceof AbstractProject ) {
                    initialize( (AbstractProject) a );
                }
            }
        }

        @Override
        public void before() {
            if( lstream != null ) {
                handler = LoggingUtils.createHandler( lstream.getOutputStream() );
                handler.addTargets( targets );
            }
        }

        @Override
        public void after() {
            /* Tear down logger */
            if( handler != null ) {
                handler.removeTargets();

                /* Flush and close handler */
                try {
                    handler.flush();
                    handler.close();
                    handler.getOut().flush();
                    handler.getOut().close();
                } catch( Exception e ) {
                    /* Unable to close handler */
                }
            }
        }

        private void initialize( AbstractBuild<?, ?> build ) {
            LoggingAction action = build.getAction( LoggingAction.class );
            if( action != null ) {
                lstream = action.getLoggingStream();
                targets = action.getTargets();

                action.getHandler().flush();
            }
        }

        private void initialize( AbstractProject<?, ?> project ) {
            LoggingJobProperty prop = (LoggingJobProperty) project.getProperty( LoggingJobProperty.class );
            if( prop != null && prop.isPollLogging() ) {
                try {
                    LoggingAction action = prop.getLoggingAction( Thread.currentThread().getId() );
                    if( action != null ) {
                        lstream = action.getLoggingStream();
                        targets = action.getTargets();
                    }
                } catch( Exception e ) {

                }
            }
        }
    }
}
