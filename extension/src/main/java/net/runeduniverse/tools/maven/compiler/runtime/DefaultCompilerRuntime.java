package net.runeduniverse.tools.maven.compiler.runtime;

import java.io.File;

import net.runeduniverse.tools.maven.compiler.api.CompilerRuntime;

public class DefaultCompilerRuntime implements CompilerRuntime {

	private File sourceDirectory;
	private File targetDirectory;

	public DefaultCompilerRuntime(File sourceDirectory, File targetDirectory) {
		this.sourceDirectory = sourceDirectory;
		this.targetDirectory = targetDirectory;
	}

	@Override
	public File getSourceDirectory() {
		return sourceDirectory;
	}

	@Override
	public File getTargetDirectory() {
		return targetDirectory;
	}

}
