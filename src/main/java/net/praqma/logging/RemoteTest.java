package net.praqma.logging;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Logger;

import hudson.model.AbstractBuild;
import hudson.remoting.VirtualChannel;

public class RemoteTest extends LoggingFileCallable<Boolean> {

	//private Logger logger = Logger.getLogger(RemoteTest.class.getName());
	
	public RemoteTest(AbstractBuild<?, ?> build) {
		super(build);
	}

	@Override
	public Boolean perform(File workspace, VirtualChannel channel)
			throws IOException, InterruptedException {
		
		//logger.info("OUTPUT!!!!");
		PrintStream out = new PrintStream( lstream.getOutputStream() );
		out.println( "Code camp 2012" );
		
		Logger logger = Logger.getLogger("codecamp");
		for( Handler h : logger.getHandlers() ) {
			out.println( "Handler " + h + ", " + h.getLevel() + ", " + h.getFormatter() );
		}
		logger.severe("MY CODE CAMP");
		
		
		return true;
	}
	
	

}
