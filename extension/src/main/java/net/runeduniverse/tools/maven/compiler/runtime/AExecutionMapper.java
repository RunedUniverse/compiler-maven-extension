package net.runeduniverse.tools.maven.compiler.runtime;

import java.io.File;

import net.runeduniverse.tools.maven.compiler.api.ExecutionMapper;

public abstract class AExecutionMapper implements ExecutionMapper {

	protected File sourceDirectory;

	protected File testSourceDirectory;

	protected File targetDirectory;

	protected File testTargetDirectory;

	protected abstract ExecutionMapper getThis();

	@Override
	public ExecutionMapper setSourceDirectory(File src) {
		this.sourceDirectory = src;
		return getThis();
	}

	@Override
	public ExecutionMapper setTestSourceDirectory(File src) {
		this.testSourceDirectory = src;
		return getThis();
	}

	@Override
	public ExecutionMapper setTargetDirectory(File target) {
		this.targetDirectory = target;
		return getThis();
	}

	@Override
	public ExecutionMapper setTestTargetDirectory(File target) {
		this.testTargetDirectory = target;
		return getThis();
	}

}
