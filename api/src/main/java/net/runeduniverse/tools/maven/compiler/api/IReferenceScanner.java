package net.runeduniverse.tools.maven.compiler.api;

import java.util.Set;

import org.apache.maven.plugin.logging.Log;

public interface IReferenceScanner {

	public IReferenceScanner inject(ICompilerRuntime runtime);

	public IReferenceScanner inject(Set<IReferenceFileScanner> fileScanner);

	public IReferenceScanner inject(IReferenceMap resultMap);

	public boolean logInfo(Log log);

	public boolean logAnalisis(Log log);

}
