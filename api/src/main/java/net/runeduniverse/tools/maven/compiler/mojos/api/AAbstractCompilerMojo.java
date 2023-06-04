package net.runeduniverse.tools.maven.compiler.mojos.api;

import org.apache.maven.plugin.AbstractMojo;
import net.runeduniverse.tools.maven.compiler.api.ICompilerRuntime;

public abstract class AAbstractCompilerMojo extends AbstractMojo {

	/**
	 * @component
	 */
	protected ICompilerRuntime runtime;

}
