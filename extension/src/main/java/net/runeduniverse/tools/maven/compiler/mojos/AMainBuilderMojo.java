package net.runeduniverse.tools.maven.compiler.mojos;

import org.apache.maven.plugin.AbstractMojo;
import org.codehaus.plexus.component.annotations.Requirement;

import net.runeduniverse.tools.maven.compiler.BuilderRuntime;
import net.runeduniverse.tools.maven.compiler.ReferenceMap;

public abstract class AMainBuilderMojo extends AbstractMojo {

	@Requirement(role = BuilderRuntime.class, hint = "builder-main")
	protected BuilderRuntime runtime;
	
	@Requirement(role = ReferenceMap.class)
	protected ReferenceMap references;
}
