package net.praqma.logging;

import hudson.FilePath;
import hudson.model.Actionable;

public abstract class RemoteLoggable extends Actionable {
	public abstract FilePath getWorkspace();
}
