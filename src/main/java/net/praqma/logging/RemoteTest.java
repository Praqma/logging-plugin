package net.praqma.logging;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Logger;

import hudson.model.AbstractBuild;
import hudson.model.Actionable;
import hudson.remoting.VirtualChannel;

public class RemoteTest extends LoggingFileCallable<Boolean> {

	//private Logger logger = Logger.getLogger(RemoteTest.class.getName());

	public RemoteTest( Actionable a ) {
		super( a );
	}

	@Override
	public Boolean perform( File workspace, VirtualChannel channel ) throws IOException, InterruptedException {

		PrintStream out = new PrintStream( lstream.getOutputStream() );
		out.println( "Code camp 2012" );

		Logger logger = Logger.getLogger( "snade.logger.remote" );
		logger.severe( "MY CODE CAMP" );
		logger.finest( "MY CODE CAMP" );

		return true;
	}

}
