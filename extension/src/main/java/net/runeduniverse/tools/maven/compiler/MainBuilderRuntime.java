package net.runeduniverse.tools.maven.compiler;

import java.io.File;

import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.component.annotations.Component;

import net.runeduniverse.tools.maven.compiler.api.BuilderRuntime;

@Component(role = BuilderRuntime.class, hint = "builder-main")
public class MainBuilderRuntime implements BuilderRuntime {
	@Parameter(property = Properties.LIFECYCLE.BUILDER.PARAM_SOURCE_DIR, defaultValue = Properties.LIFECYCLE.BUILDER.DEFAULT_VAL_SOURCE_DIR, required = true)
	File sourceDirectory;
	@Parameter(property = Properties.LIFECYCLE.BUILDER.PARAM_TARGET_DIR, defaultValue = Properties.LIFECYCLE.BUILDER.DEFAULT_VAL_TARGET_DIR, required = true)
	File targetDirectory;

	@Override
	public File getSourceDirectory() {
		return this.sourceDirectory;
	}

	@Override
	public File getTargetDirectory() {
		return this.targetDirectory;
	}

}
