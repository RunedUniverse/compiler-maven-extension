package net.runeduniverse.tools.maven.compiler.api;

import java.io.File;

public interface CompilerRuntime {

	public static final String DEFAULT_HINT = "default";

	public default String getHint() {
		return CompilerRuntime.DEFAULT_HINT;
	}

	public File getSourceDirectory();

	public File getTargetDirectory();

}
