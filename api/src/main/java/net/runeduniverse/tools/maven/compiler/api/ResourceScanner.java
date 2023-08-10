package net.runeduniverse.tools.maven.compiler.api;

import org.apache.maven.plugin.logging.Log;

public interface ResourceScanner {

	public void identifyNodes();

	public boolean scan();

	public boolean logAnalisis(Log log);

}
