package net.runeduniverse.tools.maven.compiler.api;

import java.io.File;

public interface ICompilerRuntime {

	public static final String DEFAULT_HINT = "default";

	public default String getHint() {
		return ICompilerRuntime.DEFAULT_HINT;
	}

	public File getSourceDirectory();

	public File getTargetDirectory();

}
