package net.runeduniverse.tools.maven.compiler.runtime;

import java.io.File;

import net.runeduniverse.tools.maven.compiler.api.IExecutionMapper;

public abstract class AExecutionMapper implements IExecutionMapper {

	protected File sourceDirectory;

	protected File testSourceDirectory;

	protected File targetDirectory;

	protected File testTargetDirectory;

	protected abstract IExecutionMapper getThis();

	@Override
	public IExecutionMapper setSourceDirectory(File src) {
		this.sourceDirectory = src;
		return getThis();
	}

	@Override
	public IExecutionMapper setTestSourceDirectory(File src) {
		this.testSourceDirectory = src;
		return getThis();
	}

	@Override
	public IExecutionMapper setTargetDirectory(File target) {
		this.targetDirectory = target;
		return getThis();
	}

	@Override
	public IExecutionMapper setTestTargetDirectory(File target) {
		this.testTargetDirectory = target;
		return getThis();
	}

}
