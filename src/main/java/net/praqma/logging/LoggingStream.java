package net.praqma.logging;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;

import hudson.CloseProofOutputStream;
import hudson.remoting.RemoteOutputStream;
import hudson.util.StreamTaskListener;

public class LoggingStream implements Serializable {

	private OutputStream out;
	
	public LoggingStream(OutputStream out) {
		this.out = out;
	}
	
	public OutputStream getOutputStream() {
		return out;
	}
	
    private void writeObject(ObjectOutputStream out) throws IOException {
    	CloseProofOutputStream cpos = new CloseProofOutputStream(this.out);
    	RemoteOutputStream ros = new RemoteOutputStream(cpos);
        out.writeObject( ros );
        //out.writeObject(charset==null? null : charset.name());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        //out = new FilterOutputStream((OutputStream)in.readObject());
    	out = (OutputStream)in.readObject();
        //new FilterOutputStream(out)
        //String name = (String)in.readObject();
        //charset = name==null ? null : Charset.forName(name);
    }

}
