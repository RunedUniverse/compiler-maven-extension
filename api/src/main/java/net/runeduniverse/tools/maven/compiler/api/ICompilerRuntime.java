package net.runeduniverse.tools.maven.compiler.api;

import java.io.File;

public interface ICompilerRuntime {

	public File getSourceDirectory();

	public File getTestSourceDirectory();

	public File getTargetDirectory();

	public void setSourceDirectory(File value);

	public void setTestSourceDirectory(File value);

	public void setTargetDirectory(File value);

}
