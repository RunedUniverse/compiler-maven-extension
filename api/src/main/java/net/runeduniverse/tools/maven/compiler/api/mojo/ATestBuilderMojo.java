package net.runeduniverse.tools.maven.compiler.api.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.codehaus.plexus.component.annotations.Requirement;

import net.runeduniverse.tools.maven.compiler.api.BuilderRuntime;
import net.runeduniverse.tools.maven.compiler.api.ReferenceMap;

public abstract class ATestBuilderMojo extends AbstractMojo {

	@Requirement(role = BuilderRuntime.class, hint = "builder-test")
	protected BuilderRuntime runtime;
	
	@Requirement(role = ReferenceMap.class)
	protected ReferenceMap references;
}
