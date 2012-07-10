package net.praqma.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;

import org.jvnet.hudson.test.HudsonTestCase;

import hudson.remoting.Callable;
import hudson.remoting.Channel;
import hudson.remoting.Future;
import hudson.Launcher;
import hudson.remoting.Pipe;
import hudson.remoting.VirtualChannel;
import hudson.slaves.DumbSlave;
import hudson.util.StreamCopyThread;
import hudson.util.StreamTaskListener;

public class PipeTest extends HudsonTestCase implements Serializable {

	private VirtualChannel createSlaveChannel() throws Exception {
		DumbSlave s = createSlave();
		s.toComputer().connect( false ).get();
		VirtualChannel ch = null;
		while( ch == null ) {
			ch = s.toComputer().getChannel();
			Thread.sleep( 100 );
		}
		return ch;
	}

	public void testPipingFromRemoteWithLocalLaunch() throws Exception {
		//doPipingFromRemoteTest( new Launcher.LocalLauncher( new StreamTaskListener( System.out, Charset.defaultCharset() ) ) );
	}

	public void testPipingFromRemoteWithRemoteLaunch() throws Exception {
		//doPipingFromRemoteTest( new Launcher.RemoteLauncher( new StreamTaskListener( System.out, Charset.defaultCharset() ), createSlaveChannel(), true ) );
	}

	private void doPipingFromRemoteTest( Launcher l ) throws IOException, InterruptedException, ExecutionException {
		Pipe pipe = Pipe.createRemoteToLocal( );
		Future<String> piping = l.getChannel().callAsync( new PipingCallable( pipe ) );
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Thread t = new StreamCopyThread( "Test", pipe.getIn(), os );
		t.start();

		assertEquals( "DONE", piping.get() );
		t.join();
		assertEquals( "Hello", os.toString() );

	}

	private static class PipingCallable implements Callable<String, Throwable>, Serializable {
		//private final OutputStream out;

		private final Pipe pipe;
		
		public PipingCallable( Pipe pipe ) {
			this.pipe = pipe;
		}

		public String call() throws Throwable {
			OutputStream out = pipe.getOut();
			if( out == null ) {
				//throw new IllegalStateException( "null output" );
				Channel.current();
				out = pipe.getOut();
				if( out == null ) {
					throw new IllegalStateException( "null output" );
				}
			}
			out.write( "Hello".getBytes() );
			out.close();
			return "DONE";
		}
	}
}
