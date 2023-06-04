package net.runeduniverse.tools.maven.compiler.pipeline.api;

import java.io.File;

public interface PipelineFactory {

	public Resource createResource(File file);

}
