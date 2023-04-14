package net.runeduniverse.tools.maven.compiler.api;

import java.io.File;

public interface IExecutionMapper {

	public IExecutionMapper setSourceDirectory(File src);

	public IExecutionMapper setTestSourceDirectory(File src);

	public IExecutionMapper setTargetDirectory(File target);

	public IExecutionMapper setTestTargetDirectory(File target);

	public ICompilerRuntime createRuntime();

	public boolean isTestExecution();

}
