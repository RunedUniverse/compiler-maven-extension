package net.runeduniverse.tools.maven.compiler.api;

import java.io.File;

public interface ICompilerRuntime {

	public void initialize(final File sourceDirectory, final File targetDirectory);

	public File getSourceDirectory();

	public File getTargetDirectory();

}
