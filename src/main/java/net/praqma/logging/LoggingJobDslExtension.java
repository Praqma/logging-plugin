package net.praqma.logging;

import hudson.Extension;
import javaposse.jobdsl.dsl.RequiresPlugin;
import javaposse.jobdsl.dsl.helpers.properties.PropertiesContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslExtensionMethod;

/*
```
job{
    properties{	
        logging{
            pollLogging (boolean enablePollLogging)
            pruneDays (int daysToKeepPollLogs)
            target{
                name (String targetName)
                level (String loggingLevel)
            }
        }
    }
}
```
Valid values for `level` are `SEVERE`, `WARNING`, `INFO`, `CONFIG`, `FINE`, `FINER`and `FINEST`.
```
job{
    properties{	
        logging{
            pollLogging true
            pruneDays 20
            target{
                name 'org.techworld.sonar'
                level 'FINE'
            }
        }
    }
}
```
*/

@Extension(optional = true)
public class LoggingJobDslExtension extends ContextExtensionPoint {
    
    @RequiresPlugin(id = "logging", minimumVersion="0.2.8")
    @DslExtensionMethod(context = PropertiesContext.class)
    public Object logging(Runnable closure){
        LoggingJobDslContext context = new LoggingJobDslContext();
        executeInContext(closure, context);
        
        LoggingJobProperty logging = new LoggingJobProperty(context.pollLogging, context.pruneDays);
        logging.setTargets(context.targets);
        
        return logging;
    }
}
