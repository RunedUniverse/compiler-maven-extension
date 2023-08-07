package net.runeduniverse.tools.maven.compiler.pipeline.api;

import java.io.File;

public interface PipelineFactory {

	public Node createNode(String key);

	public ResourceType createResourceType(String suffix);

	public Resource createResource(File file);

}
