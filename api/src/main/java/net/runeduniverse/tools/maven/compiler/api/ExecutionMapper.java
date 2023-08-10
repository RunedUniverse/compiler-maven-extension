package net.runeduniverse.tools.maven.compiler.api;

import java.io.File;

public interface ExecutionMapper {

	public ExecutionMapper setSourceDirectory(File src);

	public ExecutionMapper setTestSourceDirectory(File src);

	public ExecutionMapper setTargetDirectory(File target);

	public ExecutionMapper setTestTargetDirectory(File target);

	public CompilerRuntime createRuntime();

	public boolean isTestExecution();

}
