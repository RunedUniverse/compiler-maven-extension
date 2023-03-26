package net.runeduniverse.tools.maven.compiler.api.mojos;

import org.apache.maven.plugin.AbstractMojo;
import net.runeduniverse.tools.maven.compiler.api.ICompilerRuntime;
import net.runeduniverse.tools.maven.compiler.api.IReferenceMap;

public abstract class AAbstractCompilerMojo extends AbstractMojo {

	/**
	 * @component
	 */
	protected ICompilerRuntime runtime;

	/**
	 * @component
	 */
	protected IReferenceMap references;
}
